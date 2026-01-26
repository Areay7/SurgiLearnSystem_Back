package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.ResourceSharing;
import com.discussio.resourc.service.IResourceSharingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 资源共享平台 Controller
 */
@Api(value = "资源共享平台")
@RestController
@RequestMapping("/ResourceSharingController")
@CrossOrigin(origins = "*")
public class ResourceSharingController extends BaseController {
    
    private String prefix = "admin/resourceSharing";
    
    @Autowired
    private IResourceSharingService resourceSharingService;
    
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @ApiOperation(value = "根据资源类型获取资源列表", notes = "根据资源类型获取资源列表")
    @GetMapping("/listByType")
    public AjaxResult listByType(@RequestParam String resourceType) {
        try {
            return AjaxResult.success(resourceSharingService.getResourceSharingListByResourceType(resourceType));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "资源共享平台列表", notes = "资源共享平台列表")
    @GetMapping("/list")
    public ResultTable ResourceSharinglist(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceName,
            @RequestParam(required = false) String uploadUser) {
        QueryWrapper<ResourceSharing> queryWrapper = new QueryWrapper<>();
        
        // 资源名称搜索
        if (resourceName != null && !resourceName.trim().isEmpty()) {
            queryWrapper.like("resource_name", resourceName.trim());
        }
        
        // 上传者搜索
        if (uploadUser != null && !uploadUser.trim().isEmpty()) {
            queryWrapper.like("upload_user", uploadUser.trim());
        }
        
        // 资源类型筛选
        if (resourceType != null && !resourceType.trim().isEmpty()) {
            queryWrapper.eq("resource_type", resourceType.trim());
        }
        
        // 通用搜索（兼容旧接口）
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("resource_name", searchText)
                .or()
                .like("resource_desc", searchText)
            );
        }
        
        // 排序：按上传时间倒序
        queryWrapper.orderByDesc("upload_date");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<ResourceSharing> list = resourceSharingService.selectResourceSharingList(queryWrapper);
        PageInfo<ResourceSharing> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "资源共享平台新增", notes = "资源共享平台新增")
    @PostMapping("/add")
    public AjaxResult ResourceSharingadd(@RequestBody ResourceSharing resourceSharing) {
        return toAjax(resourceSharingService.insertResourceSharing(resourceSharing));
    }

    @ApiOperation(value = "资源共享平台删除", notes = "资源共享平台删除")
    @DeleteMapping("/remove")
    public AjaxResult ResourceSharingremove(@RequestParam String ids) {
        return toAjax(resourceSharingService.deleteResourceSharingByIds(ids));
    }

    @ApiOperation(value = "资源共享平台详情", notes = "获取资源共享平台详情")
    @GetMapping("/detail/{id}")
    public AjaxResult ResourceSharingdetail(@PathVariable("id") Long id) {
        return AjaxResult.success(resourceSharingService.selectResourceSharingById(id));
    }

    @ApiOperation(value = "editSave", notes = "资源共享平台修改保存")
    @PostMapping("/edit")
    public AjaxResult ResourceSharingeditSave(@RequestBody ResourceSharing resourceSharing) {
        return toAjax(resourceSharingService.updateResourceSharing(resourceSharing));
    }
    
    @ApiOperation(value = "上传资源文件", notes = "上传资源文件")
    @PostMapping("/upload")
    public AjaxResult uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("resourceName") String resourceName,
            @RequestParam("resourceType") String resourceType,
            @RequestParam(value = "resourceDesc", required = false) String resourceDesc,
            @RequestParam("uploadUser") String uploadUser) {
        try {
            if (file.isEmpty()) {
                return AjaxResult.error("文件不能为空");
            }
            
            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = uploadPath + File.separator + uniqueFileName;
            
            // 保存文件
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 保存资源信息到数据库
            ResourceSharing resourceSharing = new ResourceSharing();
            resourceSharing.setResourceName(resourceName);
            resourceSharing.setResourceType(resourceType);
            resourceSharing.setResourceDesc(resourceDesc);
            resourceSharing.setUploadUser(uploadUser);
            resourceSharing.setUploadDate(new Date());
            resourceSharing.setDownloadCount(0);
            resourceSharing.setFilePath(filePath);
            resourceSharing.setIsApproved(1); // 默认已审核
            
            int result = resourceSharingService.insertResourceSharing(resourceSharing);
            
            if (result > 0) {
                return AjaxResult.success("上传成功", resourceSharing);
            } else {
                // 如果保存失败，删除已上传的文件
                Files.deleteIfExists(targetPath);
                return AjaxResult.error("保存资源信息失败");
            }
        } catch (IOException e) {
            return AjaxResult.error("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "下载资源文件", notes = "下载资源文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadResource(@PathVariable("id") Long id) {
        try {
            ResourceSharing resource = resourceSharingService.selectResourceSharingById(id);
            if (resource == null || resource.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }
            
            File file = new File(resource.getFilePath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // 注意：下载次数由前端调用 incrementDownload 接口单独处理，这里不再增加
            // 这样可以避免重复增加下载次数
            
            Resource fileResource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + resource.getResourceName() + "\"")
                    .body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @ApiOperation(value = "增加下载次数", notes = "增加资源下载次数")
    @PostMapping("/incrementDownload/{id}")
    public AjaxResult incrementDownload(@PathVariable("id") Long id) {
        try {
            ResourceSharing resource = resourceSharingService.selectResourceSharingById(id);
            if (resource == null) {
                return AjaxResult.error("资源不存在");
            }
            resource.setDownloadCount((resource.getDownloadCount() == null ? 0 : resource.getDownloadCount()) + 1);
            boolean updated = resourceSharingService.updateById(resource);
            return toAjax(updated ? 1 : 0);
        } catch (Exception e) {
            return AjaxResult.error("操作失败：" + e.getMessage());
        }
    }
}

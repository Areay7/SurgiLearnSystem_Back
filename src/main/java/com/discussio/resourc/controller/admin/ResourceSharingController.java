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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

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
    
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @Autowired(required = false)
    private com.discussio.resourc.common.support.PermissionHelper permissionHelper;

    @ApiOperation(value = "根据资源类型获取资源列表", notes = "需 resource:view 权限")
    @GetMapping("/listByType")
    public AjaxResult listByType(@RequestParam String resourceType, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:view")) {
            return AjaxResult.error(403, "无权限查看资源");
        }
        try {
            return AjaxResult.success(resourceSharingService.getResourceSharingListByResourceType(resourceType));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "资源共享平台列表", notes = "需 resource:view 权限")
    @GetMapping("/list")
    public ResultTable ResourceSharinglist(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceName,
            @RequestParam(required = false) String uploadUser,
            HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:view")) {
            return new com.discussio.resourc.common.domain.ResultTable(403, "无权限查看资源", 0, java.util.Collections.emptyList());
        }
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

    @ApiOperation(value = "资源共享平台新增", notes = "需 resource:upload 权限")
    @PostMapping("/add")
    public AjaxResult ResourceSharingadd(@RequestBody ResourceSharing resourceSharing, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:upload")) {
            return AjaxResult.error(403, "无权限上传资源");
        }
        return toAjax(resourceSharingService.insertResourceSharing(resourceSharing));
    }

    @ApiOperation(value = "资源共享平台删除", notes = "需 resource:delete 权限")
    @DeleteMapping("/remove")
    public AjaxResult ResourceSharingremove(@RequestParam String ids, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:delete")) {
            return AjaxResult.error(403, "无权限删除资源");
        }
        return toAjax(resourceSharingService.deleteResourceSharingByIds(ids));
    }

    @ApiOperation(value = "资源共享平台详情", notes = "需 resource:view 权限")
    @GetMapping("/detail/{id}")
    public AjaxResult ResourceSharingdetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:view")) {
            return AjaxResult.error(403, "无权限查看资源");
        }
        return AjaxResult.success(resourceSharingService.selectResourceSharingById(id));
    }

    @ApiOperation(value = "editSave", notes = "需 resource:upload 权限")
    @PostMapping("/edit")
    public AjaxResult ResourceSharingeditSave(@RequestBody ResourceSharing resourceSharing, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:upload")) {
            return AjaxResult.error(403, "无权限编辑资源");
        }
        return toAjax(resourceSharingService.updateResourceSharing(resourceSharing));
    }
    
    @ApiOperation(value = "上传资源文件", notes = "需 resource:upload 权限")
    @PostMapping("/upload")
    public AjaxResult uploadResource(
            @RequestParam("file") MultipartFile file,
            @RequestParam("resourceName") String resourceName,
            @RequestParam("resourceType") String resourceType,
            @RequestParam(value = "resourceDesc", required = false) String resourceDesc,
            @RequestParam("uploadUser") String uploadUser,
            HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:upload")) {
            return AjaxResult.error(403, "无权限上传资源");
        }
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
    
    /** 构建下载文件名，确保始终带正确后缀 */
    private String buildDownloadFilename(ResourceSharing resource, File file) {
        String base = StringUtils.isNotBlank(resource.getResourceName()) ? resource.getResourceName() : file.getName();
        if (StringUtils.isBlank(base)) base = "resource";
        int lastDot = base.lastIndexOf('.');
        if (lastDot > 0 && lastDot < base.length() - 1) {
            return base;
        }
        String fileExt = "";
        String fileName = file.getName();
        if (fileName != null && fileName.contains(".")) {
            fileExt = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        }
        return base + fileExt;
    }

    @ApiOperation(value = "获取资源下载文件名", notes = "需 resource:download 权限")
    @GetMapping("/downloadFilename/{id}")
    public AjaxResult getDownloadFilename(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:download")) {
            return AjaxResult.error(403, "无权限下载资源");
        }
        try {
            ResourceSharing resource = resourceSharingService.selectResourceSharingById(id);
            if (resource == null || resource.getFilePath() == null) {
                return AjaxResult.error("资源不存在");
            }
            File file = new File(resource.getFilePath());
            if (!file.exists()) {
                return AjaxResult.error("文件不存在");
            }
            String filename = buildDownloadFilename(resource, file);
            return AjaxResult.success("ok", filename);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "下载资源文件", notes = "需 resource:download 权限")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadResource(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:download")) {
            return ResponseEntity.status(403).build();
        }
        try {
            ResourceSharing resource = resourceSharingService.selectResourceSharingById(id);
            if (resource == null || resource.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }
            
            File file = new File(resource.getFilePath());
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            Resource fileResource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            String filename = buildDownloadFilename(resource, file);
            String encodedFilename;
            try {
                encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                encodedFilename = file.getName() != null ? file.getName() : "resource";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename.replace("\"", "\\\"") + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @ApiOperation(value = "增加下载次数", notes = "需 resource:download 权限")
    @PostMapping("/incrementDownload/{id}")
    public AjaxResult incrementDownload(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "resource:download")) {
            return AjaxResult.error(403, "无权限下载资源");
        }
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

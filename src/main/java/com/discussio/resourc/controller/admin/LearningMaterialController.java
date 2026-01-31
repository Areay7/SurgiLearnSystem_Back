package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.LearningMaterial;
import com.discussio.resourc.service.ILearningMaterialService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Api(value = "学习资料管理")
@RestController
@RequestMapping("/LearningMaterialController")
@CrossOrigin(origins = "*")
public class LearningMaterialController extends BaseController {

    @Autowired
    private ILearningMaterialService materialService;

    /** 上传根目录（与 application.yml file.upload-path 一致） */
    @Value("${file.upload-path:./uploads}")
    private String uploadBasePath;

    private String getMaterialsDir() {
        return uploadBasePath + File.separator + "materials";
    }

    @Value("${file.upload-max-size-mb:500}")
    private long uploadMaxSizeMb;

    // 支持的后缀
    private static final String[] ALLOWED_EXT = new String[]{
            ".pdf", ".doc", ".docx", ".ppt", ".pptx", ".xls", ".xlsx",
            ".jpg", ".jpeg", ".png", ".gif", ".mp4", ".mp3", ".wav"
    };

    private boolean isAllowedExt(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        for (String ext : ALLOWED_EXT) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }

    @ApiOperation(value = "资料列表", notes = "分页、搜索、分类/标签/状态过滤")
    @GetMapping("/list")
    public ResultTable list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String status) {
        QueryWrapper<LearningMaterial> qw = new QueryWrapper<>();

        if (StringUtils.isNotBlank(searchText)) {
            qw.and(w -> w.like("title", searchText).or().like("description", searchText));
        }
        if (StringUtils.isNotBlank(category)) {
            qw.eq("category", category.trim());
        }
        if (StringUtils.isNotBlank(tags)) {
            // 模糊匹配标签字符串
            qw.like("tags", tags.trim());
        }
        if (StringUtils.isNotBlank(status)) {
            qw.eq("status", status.trim());
        }
        qw.orderByDesc("update_time");

        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<LearningMaterial> list = materialService.selectLearningMaterialList(qw);
        PageInfo<LearningMaterial> pageInfo = new PageInfo<>(list);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "新增资料（仅元数据）", notes = "不含文件上传")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody LearningMaterial material) {
        try {
            return toAjax(materialService.insertLearningMaterial(material));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "修改资料", notes = "修改元数据")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody LearningMaterial material) {
        try {
            return toAjax(materialService.updateLearningMaterial(material));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除资料", notes = "删除资料")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids) {
        return toAjax(materialService.deleteLearningMaterialByIds(ids));
    }

    @ApiOperation(value = "资料详情", notes = "获取资料详情")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return AjaxResult.success(materialService.selectLearningMaterialById(id));
    }

    @ApiOperation(value = "上传资料文件并创建记录", notes = "multipart 上传，含基本元数据")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "uploaderId", required = false) String uploaderId,
            @RequestParam(value = "uploaderName", required = false) String uploaderName
    ) {
        try {
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("文件不能为空");
            }
            long maxBytes = uploadMaxSizeMb * 1024L * 1024L;
            if (maxBytes > 0 && file.getSize() > maxBytes) {
                return AjaxResult.error("文件过大，最大支持 " + uploadMaxSizeMb + "MB");
            }
            String originalName = file.getOriginalFilename();
            if (!isAllowedExt(originalName)) {
                return AjaxResult.error("不支持的文件类型，仅支持: pdf/doc/docx/ppt/pptx/xls/xlsx/jpg/jpeg/png/gif/mp4/mp3/wav");
            }

            String materialsDir = getMaterialsDir();
            File dir = new File(materialsDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }
            String uniqueName = UUID.randomUUID().toString() + ext;
            Path target = Paths.get(materialsDir, uniqueName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            LearningMaterial material = new LearningMaterial();
            material.setTitle(title);
            material.setDescription(description);
            material.setCategory(category);
            material.setTags(tags);
            material.setFileType(ext);
            material.setFileSize(file.getSize());
            material.setFilePath(target.toString());
            material.setOriginalName(originalName);
            material.setUploaderId(uploaderId);
            material.setUploaderName(uploaderName);
            material.setStatus(StringUtils.isNotBlank(status) ? status : "已发布");
            material.setCreateTime(new Date());
            material.setUpdateTime(new Date());
            material.setViewCount(0);
            material.setDownloadCount(0);

            int inserted = materialService.insertLearningMaterial(material);
            if (inserted > 0) {
                return AjaxResult.success("上传成功", material);
            } else {
                Files.deleteIfExists(target);
                return AjaxResult.error("保存失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

    /** 解析资料文件：若为相对路径则基于上传根目录解析 */
    private File resolveFile(String storedPath) {
        if (storedPath == null) return null;
        File f = new File(storedPath);
        if (f.isAbsolute() && f.exists()) return f;
        File underBase = new File(uploadBasePath, storedPath);
        if (underBase.exists()) return underBase;
        File underMaterials = new File(getMaterialsDir(), new File(storedPath).getName());
        return underMaterials.exists() ? underMaterials : f;
    }

    /**
     * 构建下载文件名，确保始终带正确后缀（避免下载后无扩展名）
     */
    private String buildDownloadFilename(LearningMaterial material, File file) {
        String base = StringUtils.isNotBlank(material.getOriginalName()) ? material.getOriginalName() : file.getName();
        if (StringUtils.isBlank(base)) base = "download";
        // 若已有后缀则直接返回
        int lastDot = base.lastIndexOf('.');
        if (lastDot > 0 && lastDot < base.length() - 1) {
            return base;
        }
        // 从磁盘文件补全后缀
        String fileExt = "";
        String fileName = file.getName();
        if (fileName != null && fileName.contains(".")) {
            fileExt = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        }
        if (StringUtils.isBlank(fileExt) && StringUtils.isNotBlank(material.getFileType())) {
            fileExt = material.getFileType().startsWith(".") ? material.getFileType() : "." + material.getFileType();
        }
        return base + fileExt;
    }

    @ApiOperation(value = "获取资料下载文件名", notes = "用于前端下载时设置正确文件名")
    @GetMapping("/downloadFilename/{id}")
    public AjaxResult getDownloadFilename(@PathVariable("id") Long id) {
        try {
            LearningMaterial material = materialService.selectLearningMaterialById(id);
            if (material == null || material.getFilePath() == null) {
                return AjaxResult.error("资料不存在");
            }
            File file = resolveFile(material.getFilePath());
            if (file == null || !file.exists()) {
                return AjaxResult.error("文件不存在");
            }
            String filename = buildDownloadFilename(material, file);
            return AjaxResult.success("ok", filename);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "下载资料文件", notes = "根据ID下载")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) {
        try {
            LearningMaterial material = materialService.selectLearningMaterialById(id);
            if (material == null || material.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }
            File file = resolveFile(material.getFilePath());
            if (file == null || !file.exists()) {
                return ResponseEntity.notFound().build();
            }
            Resource res = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) contentType = "application/octet-stream";
            String filename = buildDownloadFilename(material, file);
            String encodedFilename;
            try {
                encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.name()).replace("+", "%20");
            } catch (UnsupportedEncodingException e) {
                encodedFilename = file.getName() != null ? file.getName() : "download";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename.replace("\"", "\\\"") + "\"; filename*=UTF-8''" + encodedFilename)
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation(value = "预览资料文件", notes = "根据ID在线预览（浏览器内打开）")
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> preview(@PathVariable("id") Long id) {
        try {
            LearningMaterial material = materialService.selectLearningMaterialById(id);
            if (material == null || material.getFilePath() == null) {
                return ResponseEntity.notFound().build();
            }
            File file = resolveFile(material.getFilePath());
            if (file == null || !file.exists()) {
                return ResponseEntity.notFound().build();
            }
            Resource res = new FileSystemResource(file);
            String contentType = null;
            // 优先根据后缀判断常见预览类型，保证浏览器可以正确内嵌展示
            String name = file.getName().toLowerCase();
            if (name.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (name.endsWith(".png")) {
                contentType = "image/png";
            } else if (name.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (name.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else {
                try {
                    contentType = Files.probeContentType(file.toPath());
                } catch (Exception ignored) {
                }
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            }
            ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + (material.getOriginalName() != null ? material.getOriginalName() : file.getName()) + "\"");
            // 视频/大文件：支持 Range 请求，浏览器可流式播放不必一次拉取全部
            if (name.endsWith(".mp4") || name.endsWith(".pdf")) {
                builder.header("Accept-Ranges", "bytes");
            }
            return builder.body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ApiOperation(value = "增加下载次数", notes = "下载统计")
    @PostMapping("/incrementDownload/{id}")
    public AjaxResult incrementDownload(@PathVariable("id") Long id) {
        LearningMaterial m = materialService.selectLearningMaterialById(id);
        if (m == null) return AjaxResult.error("资料不存在");
        int count = m.getDownloadCount() == null ? 0 : m.getDownloadCount();
        m.setDownloadCount(count + 1);
        return toAjax(materialService.updateLearningMaterial(m));
    }

    @ApiOperation(value = "增加浏览次数", notes = "浏览统计")
    @PostMapping("/incrementView/{id}")
    public AjaxResult incrementView(@PathVariable("id") Long id) {
        LearningMaterial m = materialService.selectLearningMaterialById(id);
        if (m == null) return AjaxResult.error("资料不存在");
        int count = m.getViewCount() == null ? 0 : m.getViewCount();
        m.setViewCount(count + 1);
        return toAjax(materialService.updateLearningMaterial(m));
    }
}

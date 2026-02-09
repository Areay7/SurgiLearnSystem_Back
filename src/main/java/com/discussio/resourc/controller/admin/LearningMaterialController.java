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

import javax.servlet.http.HttpServletRequest;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    @Autowired(required = false)
    private com.discussio.resourc.common.support.PermissionHelper permissionHelper;

    private static final Map<String, Object[]> PREVIEW_TOKEN_CACHE = new ConcurrentHashMap<>();
    private static final long PREVIEW_TOKEN_EXPIRE_MS = 600000; // 10分钟有效
    private static final Map<String, Object[]> DOWNLOAD_TOKEN_CACHE = new ConcurrentHashMap<>();
    private static final long DOWNLOAD_TOKEN_EXPIRE_MS = 120000; // 2分钟有效

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

    @ApiOperation(value = "资料列表", notes = "需 material:view 权限")
    @GetMapping("/list")
    public ResultTable list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:view")) {
            return new com.discussio.resourc.common.domain.ResultTable(403, "无权限查看资料", 0, java.util.Collections.emptyList());
        }
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

    @ApiOperation(value = "新增资料（仅元数据）", notes = "需 material:create 权限")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody LearningMaterial material, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:create")) {
            return AjaxResult.error(403, "无权限新增资料");
        }
        try {
            return toAjax(materialService.insertLearningMaterial(material));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "修改资料", notes = "需 material:edit 权限")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody LearningMaterial material, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:edit")) {
            return AjaxResult.error(403, "无权限编辑资料");
        }
        try {
            return toAjax(materialService.updateLearningMaterial(material));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除资料", notes = "需 material:delete 权限")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:delete")) {
            return AjaxResult.error(403, "无权限删除资料");
        }
        return toAjax(materialService.deleteLearningMaterialByIds(ids));
    }

    @ApiOperation(value = "资料详情", notes = "需 material:view 权限")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:view")) {
            return AjaxResult.error(403, "无权限查看资料");
        }
        return AjaxResult.success(materialService.selectLearningMaterialById(id));
    }

    @ApiOperation(value = "上传资料文件并创建记录", notes = "需 material:create 权限")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "uploaderId", required = false) String uploaderId,
            @RequestParam(value = "uploaderName", required = false) String uploaderName,
            HttpServletRequest req
    ) {
        if (permissionHelper != null && !permissionHelper.hasPermission(req, "material:create")) {
            return AjaxResult.error(403, "无权限上传资料");
        }
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

    @ApiOperation(value = "获取资料下载文件名", notes = "需 material:download 权限")
    @GetMapping("/downloadFilename/{id}")
    public AjaxResult getDownloadFilename(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:download")) {
            return AjaxResult.error(403, "无权限下载资料");
        }
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

    @ApiOperation(value = "获取下载地址（带临时token，用于导航下载，避免鉴权与CORS）")
    @GetMapping("/downloadUrl/{id}")
    public AjaxResult getDownloadUrl(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:download")) {
            return AjaxResult.error(403, "无权限下载");
        }
        LearningMaterial material = materialService.selectLearningMaterialById(id);
        if (material == null || material.getFilePath() == null) {
            return AjaxResult.error("资料不存在");
        }
        File file = resolveFile(material.getFilePath());
        if (file == null || !file.exists()) {
            return AjaxResult.error("文件不存在");
        }
        String token = UUID.randomUUID().toString();
        long expireAt = System.currentTimeMillis() + DOWNLOAD_TOKEN_EXPIRE_MS;
        DOWNLOAD_TOKEN_CACHE.put(token, new Object[]{id, Long.valueOf(expireAt)});
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String url = baseUrl + "/LearningMaterialController/download/" + id + "?token=" + token;
        return AjaxResult.success("ok", url);
    }

    @ApiOperation(value = "下载资料文件（支持 Bearer 或 token 参数）")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String token,
            HttpServletRequest request) {
        boolean allowed = false;
        if (token != null && !token.trim().isEmpty()) {
            Object[] cached = DOWNLOAD_TOKEN_CACHE.remove(token.trim());
            if (cached != null) {
                Long expireAt = (Long) cached[1];
                if (System.currentTimeMillis() < expireAt.longValue() && id.equals(cached[0])) {
                    allowed = true;
                }
            }
        }
        if (!allowed && (permissionHelper != null && !permissionHelper.hasPermission(request, "material:download"))) {
            return ResponseEntity.status(403).build();
        }
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

    @ApiOperation(value = "获取预览地址（带临时token，用于 img/video/iframe，避免鉴权与CORS）")
    @GetMapping("/previewUrl/{id}")
    public AjaxResult getPreviewUrl(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:view")) {
            return AjaxResult.error(403, "无权限预览");
        }
        LearningMaterial material = materialService.selectLearningMaterialById(id);
        if (material == null || material.getFilePath() == null) {
            return AjaxResult.error("资料不存在");
        }
        File file = resolveFile(material.getFilePath());
        if (file == null || !file.exists()) {
            return AjaxResult.error("文件不存在");
        }
        String token = UUID.randomUUID().toString();
        long expireAt = System.currentTimeMillis() + PREVIEW_TOKEN_EXPIRE_MS;
        PREVIEW_TOKEN_CACHE.put(token, new Object[]{id, Long.valueOf(expireAt)});
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String url = baseUrl + "/LearningMaterialController/preview/" + id + "?token=" + token;
        return AjaxResult.success("ok", url);
    }

    @ApiOperation(value = "预览资料文件（支持 Bearer 或 token 参数）")
    @GetMapping("/preview/{id}")
    public ResponseEntity<Resource> preview(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String token,
            HttpServletRequest request) {
        boolean allowed = false;
        if (token != null && !token.trim().isEmpty()) {
            Object[] cached = PREVIEW_TOKEN_CACHE.get(token.trim());
            if (cached != null) {
                Long expireAt = (Long) cached[1];
                if (System.currentTimeMillis() < expireAt.longValue() && id.equals(cached[0])) {
                    allowed = true;
                }
            }
        }
        if (!allowed && (permissionHelper != null && !permissionHelper.hasPermission(request, "material:view"))) {
            return ResponseEntity.status(403).build();
        }
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

    @ApiOperation(value = "增加下载次数", notes = "需 material:download 权限")
    @PostMapping("/incrementDownload/{id}")
    public AjaxResult incrementDownload(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:download")) {
            return AjaxResult.error(403, "无权限下载资料");
        }
        LearningMaterial m = materialService.selectLearningMaterialById(id);
        if (m == null) return AjaxResult.error("资料不存在");
        int count = m.getDownloadCount() == null ? 0 : m.getDownloadCount();
        m.setDownloadCount(count + 1);
        return toAjax(materialService.updateLearningMaterial(m));
    }

    @ApiOperation(value = "增加浏览次数", notes = "需 material:view 权限")
    @PostMapping("/incrementView/{id}")
    public AjaxResult incrementView(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "material:view")) {
            return AjaxResult.error(403, "无权限查看资料");
        }
        LearningMaterial m = materialService.selectLearningMaterialById(id);
        if (m == null) return AjaxResult.error("资料不存在");
        int count = m.getViewCount() == null ? 0 : m.getViewCount();
        m.setViewCount(count + 1);
        return toAjax(materialService.updateLearningMaterial(m));
    }
}

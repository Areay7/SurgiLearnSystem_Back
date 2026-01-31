package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.common.support.PermissionHelper;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.SystemSettings;
import com.discussio.resourc.service.ISystemSettingsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 系统设置选项 Controller
 */
@Api(value = "系统设置选项")
@RestController
@RequestMapping("/SystemSettingsController")
@CrossOrigin(origins = "*")
public class SystemSettingsController extends BaseController {
    
    @Autowired
    private ISystemSettingsService systemSettingsService;
    @Autowired(required = false)
    private PermissionHelper permissionHelper;
    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    private boolean hasSettingsPermission(HttpServletRequest request) {
        return permissionHelper == null || permissionHelper.hasPermission(request, "system:settings");
    }

    @ApiOperation(value = "获取系统配置", notes = "系统名称、每页显示、Logo、安全设置")
    @GetMapping("/config")
    public AjaxResult getConfig(HttpServletRequest request) {
        if (!hasSettingsPermission(request)) return AjaxResult.error(403, "无权限");
        SystemSettings config = systemSettingsService.getOrCreateConfig();
        return AjaxResult.success(config);
    }

    @ApiOperation(value = "获取系统显示配置", notes = "公开接口，无需登录，用于登录页和布局显示系统名称、Logo")
    @GetMapping("/config/display")
    public AjaxResult getDisplayConfig() {
        SystemSettings config = systemSettingsService.getOrCreateConfig();
        java.util.Map<String, Object> display = new java.util.HashMap<>();
        display.put("systemName", config.getSystemName() != null ? config.getSystemName() : "外科护理主管护师培训学习系统");
        display.put("systemLogo", config.getSystemLogo());
        return AjaxResult.success(display);
    }

    @ApiOperation(value = "保存系统配置")
    @PostMapping("/config")
    public AjaxResult saveConfig(@RequestBody SystemSettings config, HttpServletRequest request) {
        if (!hasSettingsPermission(request)) return AjaxResult.error(403, "无权限");
        return toAjax(systemSettingsService.saveConfig(config));
    }

    @ApiOperation(value = "上传系统Logo")
    @PostMapping("/uploadLogo")
    public AjaxResult uploadLogo(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (!hasSettingsPermission(request)) return AjaxResult.error(403, "无权限");
        try {
            if (file == null || file.isEmpty()) return AjaxResult.error("文件不能为空");
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.matches(".*\\.(png|jpg|jpeg|gif|svg|webp)$")) {
                return AjaxResult.error("请上传图片格式（png/jpg/jpeg/gif/svg/webp）");
            }
            String ext = originalName.substring(originalName.lastIndexOf("."));
            String fileName = "logo_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8) + ext;
            String uploadDir = uploadPath + File.separator + "system";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(dir, fileName);
            file.transferTo(dest);
            String relativePath = "system/" + fileName;
            systemSettingsService.updateLogoPath(relativePath);
            return AjaxResult.success("上传成功", relativePath);
        } catch (Exception e) {
            return AjaxResult.error("上传失败: " + e.getMessage());
        }
    }

    @ApiOperation(value = "获取系统Logo图片")
    @GetMapping("/logo")
    public ResponseEntity<Resource> getLogo() {
        SystemSettings config = systemSettingsService.getOrCreateConfig();
        String logoPath = config.getSystemLogo();
        if (logoPath == null || logoPath.trim().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        File file = Paths.get(uploadPath, logoPath).toFile();
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        String name = file.getName().toLowerCase();
        MediaType mt = name.endsWith(".png") ? MediaType.IMAGE_PNG : name.endsWith(".gif") ? MediaType.IMAGE_GIF
                : name.endsWith(".svg") ? MediaType.valueOf("image/svg+xml") : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(mt).body(resource);
    }

    @ApiOperation(value = "系统设置选项列表", notes = "系统设置选项列表")
    @GetMapping("/list")
    public ResultTable SystemSettingslist(Tablepar tablepar) {
        QueryWrapper<SystemSettings> queryWrapper = new QueryWrapper<>();
        if (tablepar != null && tablepar.getSearchText() != null && !tablepar.getSearchText().isEmpty()) {
            queryWrapper.like("course_type", tablepar.getSearchText());
        }
        
        PageHelper.startPage(tablepar != null && tablepar.getPage() != null ? tablepar.getPage() : 1, 
                           tablepar != null && tablepar.getLimit() != null ? tablepar.getLimit() : 10);
        List<SystemSettings> list = systemSettingsService.list(queryWrapper);
        PageInfo<SystemSettings> page = new PageInfo<>(list);
        
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "系统设置选项新增", notes = "系统设置选项新增")
    @PostMapping("/add")
    public AjaxResult SystemSettingsadd(@RequestBody SystemSettings systemSettings) {
        return toAjax(systemSettingsService.insertSystemSettings(systemSettings));
    }

    @ApiOperation(value = "系统设置选项删除", notes = "系统设置选项删除")
    @DeleteMapping("/remove")
    public AjaxResult SystemSettingsremove(@RequestParam String ids) {
        return toAjax(systemSettingsService.deleteSystemSettingsByIds(ids));
    }

    @ApiOperation(value = "系统设置选项详情", notes = "获取系统设置选项详情")
    @GetMapping("/detail/{id}")
    public AjaxResult SystemSettingsdetail(@PathVariable("id") Long id) {
        return AjaxResult.success(systemSettingsService.selectSystemSettingsById(id));
    }

    @ApiOperation(value = "editSave", notes = "系统设置选项修改保存")
    @PostMapping("/edit")
    public AjaxResult SystemSettingseditSave(@RequestBody SystemSettings systemSettings) {
        return toAjax(systemSettingsService.updateSystemSettings(systemSettings));
    }
}

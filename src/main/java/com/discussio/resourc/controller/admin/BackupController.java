package com.discussio.resourc.controller.admin;

import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.common.support.PermissionHelper;
import com.discussio.resourc.model.auto.BackupConfig;
import com.discussio.resourc.model.auto.BackupRecord;
import com.discussio.resourc.mapper.auto.BackupRecordMapper;
import com.discussio.resourc.service.IBackupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * 数据备份 Controller（需 system:backup 权限）
 */
@Api(value = "数据备份")
@RestController
@RequestMapping("/BackupController")
@CrossOrigin(origins = "*")
public class BackupController extends BaseController {

    @Autowired
    private IBackupService backupService;
    @Autowired
    private BackupRecordMapper backupRecordMapper;
    @Autowired(required = false)
    private PermissionHelper permissionHelper;

    private boolean hasBackupPermission(HttpServletRequest request) {
        return permissionHelper != null && permissionHelper.hasPermission(request, "system:backup");
    }

    @ApiOperation(value = "获取备份配置")
    @GetMapping("/config")
    public AjaxResult getConfig(HttpServletRequest request) {
        if (!hasBackupPermission(request)) return AjaxResult.error(403, "无权限");
        return AjaxResult.success(backupService.getConfig());
    }

    @ApiOperation(value = "保存备份配置")
    @PostMapping("/config")
    public AjaxResult saveConfig(@RequestBody BackupConfig config, HttpServletRequest request) {
        if (!hasBackupPermission(request)) return AjaxResult.error(403, "无权限");
        return toAjax(backupService.saveConfig(config));
    }

    @ApiOperation(value = "执行备份")
    @PostMapping("/execute")
    public AjaxResult execute(
            @RequestParam(required = false) String backupPath,
            @RequestParam(required = false, defaultValue = "true") Boolean includeUploads,
            @RequestParam(required = false, defaultValue = "true") Boolean includeDatabase,
            HttpServletRequest request) {
        if (!hasBackupPermission(request)) return AjaxResult.error(403, "无权限");
        BackupConfig cfg = backupService.getConfig();
        String path = backupPath != null && !backupPath.trim().isEmpty()
                ? backupPath.trim() : (cfg.getBackupPath() != null ? cfg.getBackupPath() : null);
        BackupRecord record = backupService.executeBackup(path,
                includeUploads != null && includeUploads,
                includeDatabase != null && includeDatabase,
                false);
        if ("success".equals(record.getStatus())) {
            return AjaxResult.success("备份成功", record);
        }
        return AjaxResult.error(record.getErrorMsg() != null ? record.getErrorMsg() : "备份失败");
    }

    @ApiOperation(value = "备份记录列表")
    @GetMapping("/list")
    public ResultTable list(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            HttpServletRequest request) {
        if (!hasBackupPermission(request)) {
            return new ResultTable(403, "无权限", 0, java.util.Collections.emptyList());
        }
        List<BackupRecord> list = backupService.listRecords(page, limit);
        long total = backupService.countRecords();
        return pageTable(list, total);
    }

    @ApiOperation(value = "下载备份文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id, HttpServletRequest request) {
        if (!hasBackupPermission(request)) {
            return ResponseEntity.status(403).build();
        }
        BackupRecord record = backupRecordMapper.selectById(id);
        if (record == null || record.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(record.getFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        String fileName = record.getFileName() != null ? record.getFileName() : file.getName();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @ApiOperation(value = "删除备份记录")
    @DeleteMapping("/remove/{id}")
    public AjaxResult remove(@PathVariable Long id, HttpServletRequest request) {
        if (!hasBackupPermission(request)) return AjaxResult.error(403, "无权限");
        return toAjax(backupService.deleteRecord(id));
    }

    @ApiOperation(value = "清理过期备份")
    @PostMapping("/cleanup")
    public AjaxResult cleanup(
            @RequestParam(required = false) Integer retentionDays,
            HttpServletRequest request) {
        if (!hasBackupPermission(request)) return AjaxResult.error(403, "无权限");
        if (retentionDays == null) {
            BackupConfig cfg = backupService.getConfig();
            retentionDays = cfg.getRetentionDays() != null ? cfg.getRetentionDays() : 30;
        }
        backupService.cleanupByRetention(retentionDays);
        return AjaxResult.success("清理完成");
    }
}

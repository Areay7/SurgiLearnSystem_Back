package com.discussio.resourc.service;

import com.discussio.resourc.model.auto.BackupConfig;
import com.discussio.resourc.model.auto.BackupRecord;

import java.util.List;

/**
 * 备份服务接口
 */
public interface IBackupService {
    BackupConfig getConfig();
    int saveConfig(BackupConfig config);
    BackupRecord executeBackup(String backupPath, boolean includeUploads, boolean includeDatabase, boolean isAuto);
    List<BackupRecord> listRecords(int page, int limit);
    long countRecords();
    int deleteRecord(Long id);
    void cleanupByRetention(int retentionDays);
}

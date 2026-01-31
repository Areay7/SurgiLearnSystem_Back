package com.discussio.resourc.task;

import com.discussio.resourc.model.auto.BackupConfig;
import com.discussio.resourc.model.auto.BackupRecord;
import com.discussio.resourc.service.IBackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * 定时备份任务：每小时检查一次，若匹配配置时间则执行备份
 */
@Component
public class BackupScheduler {

    private static final Logger log = LoggerFactory.getLogger(BackupScheduler.class);

    @Autowired
    private IBackupService backupService;

    @Scheduled(cron = "0 0 * * * ?")  // 每小时整点
    public void checkAndRunBackup() {
        try {
            BackupConfig cfg = backupService.getConfig();
            if (cfg == null || cfg.getAutoEnabled() == null || cfg.getAutoEnabled() != 1) {
                return;
            }
            String scheduleTime = cfg.getScheduleTime();
            if (scheduleTime == null || scheduleTime.trim().isEmpty()) {
                scheduleTime = "02:00";
            }
            String[] parts = scheduleTime.trim().split(":");
            int targetHour = 2;
            int targetMin = 0;
            if (parts.length >= 1) {
                try {
                    targetHour = Integer.parseInt(parts[0].trim());
                } catch (NumberFormatException ignored) {}
            }
            if (parts.length >= 2) {
                try {
                    targetMin = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException ignored) {}
            }

            Calendar now = Calendar.getInstance();
            if (now.get(Calendar.HOUR_OF_DAY) != targetHour || now.get(Calendar.MINUTE) != targetMin) {
                return;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd").format(now.getTime());
            List<BackupRecord> recent = backupService.listRecords(1, 10);
            for (BackupRecord r : recent) {
                if (r.getCreateTime() != null && "auto".equals(r.getBackupType())) {
                    String recDate = new SimpleDateFormat("yyyy-MM-dd").format(r.getCreateTime());
                    if (today.equals(recDate)) {
                        log.debug("今日自动备份已执行，跳过");
                        return;
                    }
                }
            }

            String path = cfg.getBackupPath();
            boolean incUploads = cfg.getIncludeUploads() == null || cfg.getIncludeUploads() == 1;
            boolean incDb = cfg.getIncludeDatabase() == null || cfg.getIncludeDatabase() == 1;
            BackupRecord record = backupService.executeBackup(path, incUploads, incDb, true);
            log.info("自动备份完成: {}", record.getStatus());

            Integer retention = cfg.getRetentionDays();
            if (retention != null && retention > 0) {
                backupService.cleanupByRetention(retention);
            }
        } catch (Exception e) {
            log.error("自动备份失败", e);
        }
    }
}

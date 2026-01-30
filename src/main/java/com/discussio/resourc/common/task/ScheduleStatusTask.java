package com.discussio.resourc.common.task;

import com.discussio.resourc.service.IScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 课程安排状态自动刷新任务
 */
@Component
public class ScheduleStatusTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleStatusTask.class);

    private final IScheduleService scheduleService;

    public ScheduleStatusTask(IScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 每分钟执行一次（秒级对齐）
    @Scheduled(cron = "5 * * * * ?")
    public void refresh() {
        try {
            int changed = scheduleService.refreshStatusesNow();
            if (changed > 0) {
                logger.info("课程安排状态自动刷新：变更 {} 条", changed);
            }
        } catch (Exception e) {
            logger.warn("课程安排状态自动刷新失败: {}", e.getMessage());
        }
    }
}


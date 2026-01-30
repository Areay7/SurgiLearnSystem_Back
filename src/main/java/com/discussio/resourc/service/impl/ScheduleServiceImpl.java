package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.ScheduleMapper;
import com.discussio.resourc.model.auto.Schedule;
import com.discussio.resourc.service.IScheduleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 课程安排设置 Service 实现
 */
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements IScheduleService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");

    @Override
    public Schedule selectScheduleById(Long id) {
        return this.baseMapper.selectScheduleById(id);
    }

    @Override
    public List<Schedule> selectScheduleList(Wrapper<Schedule> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertSchedule(Schedule schedule) {
        if (StringUtils.isBlank(schedule.getCourseName())) {
            throw new RuntimeException("课程名称不能为空！");
        }
        if (schedule.getScheduleDate() == null) {
            throw new RuntimeException("安排日期不能为空！");
        }
        if (StringUtils.isBlank(schedule.getInstructorName())) {
            throw new RuntimeException("讲师不能为空！");
        }

        if (schedule.getScheduleId() == null) {
            schedule.setScheduleId(System.currentTimeMillis());
        }
        if (schedule.getCreateTime() == null) {
            schedule.setCreateTime(new Date());
        }
        if (schedule.getUpdateTime() == null) {
            schedule.setUpdateTime(new Date());
        }
        if (StringUtils.isBlank(schedule.getStatus())) {
            schedule.setStatus("未开始");
        }
        if (schedule.getEnrolledStudents() == null) {
            schedule.setEnrolledStudents(0);
        }
        if (schedule.getMaxStudents() == null) {
            schedule.setMaxStudents(0);
        }
        // 保存时按当前时间计算一次状态（防止手动传错）
        schedule.setStatus(calcStatusFor(schedule, LocalDateTime.now(ZONE)));
        return this.baseMapper.insert(schedule);
    }

    @Override
    public int updateSchedule(Schedule schedule) {
        if (schedule.getId() == null) {
            throw new RuntimeException("课程安排ID不能为空！");
        }
        // 更新时按当前时间计算一次状态（支持到点自动变化，避免用户误填）
        schedule.setStatus(calcStatusFor(schedule, LocalDateTime.now(ZONE)));
        schedule.setUpdateTime(new Date());
        return this.baseMapper.updateById(schedule);
    }

    @Override
    public int deleteScheduleByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteScheduleById(Long id) {
        if (id == null) {
            throw new RuntimeException("课程安排ID不能为空！");
        }
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int refreshStatusesNow() {
        // 为了保证“到点自动开始/结束”，这里直接按当前时间计算并更新状态
        LocalDateTime now = LocalDateTime.now(ZONE);

        QueryWrapper<Schedule> qw = new QueryWrapper<>();
        // 只处理可能变化的：未开始/进行中；已结束的不再更新（避免反复写库）
        qw.and(w -> w.eq("status", "未开始").or().eq("status", "进行中").or().isNull("status"));
        List<Schedule> list = this.baseMapper.selectList(qw);
        if (list == null || list.isEmpty()) return 0;

        int changed = 0;
        for (Schedule s : list) {
            try {
                if (s.getScheduleDate() == null) continue;
                LocalDate date = Instant.ofEpochMilli(s.getScheduleDate().getTime()).atZone(ZONE).toLocalDate();

                LocalTime start = parseTimeOrDefault(s.getStartTime(), LocalTime.of(0, 0));
                LocalTime end = parseTimeOrDefault(s.getEndTime(), LocalTime.of(23, 59, 59));

                LocalDateTime startAt = LocalDateTime.of(date, start);
                LocalDateTime endAt = LocalDateTime.of(date, end);

                String newStatus;
                if (now.isBefore(startAt)) newStatus = "未开始";
                else if (now.isAfter(endAt)) newStatus = "已结束";
                else newStatus = "进行中";

                String old = (s.getStatus() == null || s.getStatus().trim().isEmpty()) ? "未开始" : s.getStatus().trim();
                if (!newStatus.equals(old)) {
                    Schedule upd = new Schedule();
                    upd.setId(s.getId());
                    upd.setStatus(newStatus);
                    upd.setUpdateTime(new Date());
                    this.baseMapper.updateById(upd);
                    changed++;
                }
            } catch (Exception ignore) {
                // 单条失败不影响整体
            }
        }
        return changed;
    }

    private LocalTime parseTimeOrDefault(String time, LocalTime def) {
        if (time == null || time.trim().isEmpty()) return def;
        String t = time.trim();
        try {
            return LocalTime.parse(t, TIME_FMT);
        } catch (Exception e) {
            // 兼容 "HH:mm:ss"
            try {
                return LocalTime.parse(t);
            } catch (Exception ex) {
                return def;
            }
        }
    }

    private String calcStatusFor(Schedule s, LocalDateTime now) {
        if (s == null || s.getScheduleDate() == null) return "未开始";
        LocalDate date = Instant.ofEpochMilli(s.getScheduleDate().getTime()).atZone(ZONE).toLocalDate();
        LocalTime start = parseTimeOrDefault(s.getStartTime(), LocalTime.of(0, 0));
        LocalTime end = parseTimeOrDefault(s.getEndTime(), LocalTime.of(23, 59, 59));
        LocalDateTime startAt = LocalDateTime.of(date, start);
        LocalDateTime endAt = LocalDateTime.of(date, end);
        if (now.isBefore(startAt)) return "未开始";
        if (now.isAfter(endAt)) return "已结束";
        return "进行中";
    }
}


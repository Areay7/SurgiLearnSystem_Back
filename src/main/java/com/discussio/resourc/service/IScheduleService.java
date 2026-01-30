package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Schedule;

import java.util.List;

/**
 * 课程安排设置 Service 接口
 */
public interface IScheduleService extends IService<Schedule> {

    Schedule selectScheduleById(Long id);

    List<Schedule> selectScheduleList(Wrapper<Schedule> queryWrapper);

    int insertSchedule(Schedule schedule);

    int updateSchedule(Schedule schedule);

    int deleteScheduleByIds(String ids);

    int deleteScheduleById(Long id);

    /**
     * 根据当前时间自动刷新课程安排状态（未开始/进行中/已结束）
     * @return 本次发生状态变更的条数
     */
    int refreshStatusesNow();
}


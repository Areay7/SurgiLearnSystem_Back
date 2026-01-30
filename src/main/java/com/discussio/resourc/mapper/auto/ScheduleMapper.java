package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.Schedule;
import org.apache.ibatis.annotations.Select;

/**
 * 课程安排设置 Mapper
 */
public interface ScheduleMapper extends BaseMapper<Schedule> {

    @Select("SELECT * FROM schedule WHERE id = #{id}")
    Schedule selectScheduleById(Long id);
}


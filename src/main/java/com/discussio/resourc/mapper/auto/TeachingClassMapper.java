package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.TeachingClass;
import org.apache.ibatis.annotations.Select;

public interface TeachingClassMapper extends BaseMapper<TeachingClass> {
    @Select("SELECT * FROM teaching_class WHERE id = #{id}")
    TeachingClass selectByIdSimple(Long id);
}


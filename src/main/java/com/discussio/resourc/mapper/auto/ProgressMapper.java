package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.Progress;
import org.apache.ibatis.annotations.Select;

/**
 * 学习进度 Mapper
 */
public interface ProgressMapper extends BaseMapper<Progress> {

    @Select("SELECT * FROM progress WHERE id = #{id}")
    Progress selectProgressById(Long id);
}


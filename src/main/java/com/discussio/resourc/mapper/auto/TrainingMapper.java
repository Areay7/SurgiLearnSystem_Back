package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.Training;
import org.apache.ibatis.annotations.Select;

/**
 * 护理培训 Mapper
 */
public interface TrainingMapper extends BaseMapper<Training> {

    @Select("SELECT * FROM training WHERE id = #{id}")
    Training selectTrainingById(Long id);
}


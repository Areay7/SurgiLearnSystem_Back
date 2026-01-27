package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.LearningMaterial;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LearningMaterialMapper extends BaseMapper<LearningMaterial> {
    LearningMaterial selectLearningMaterialById(Long id);
}

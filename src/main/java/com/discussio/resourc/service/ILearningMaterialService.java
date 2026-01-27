package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.LearningMaterial;

import java.util.List;

public interface ILearningMaterialService extends IService<LearningMaterial> {
    LearningMaterial selectLearningMaterialById(Long id);
    List<LearningMaterial> selectLearningMaterialList(Wrapper<LearningMaterial> queryWrapper);
    int insertLearningMaterial(LearningMaterial material);
    int updateLearningMaterial(LearningMaterial material);
    int deleteLearningMaterialByIds(String ids);
    int deleteLearningMaterialById(Long id);
}

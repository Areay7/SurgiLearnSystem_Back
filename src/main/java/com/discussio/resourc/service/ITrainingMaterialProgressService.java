package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.TrainingMaterialProgress;

import java.util.List;

public interface ITrainingMaterialProgressService extends IService<TrainingMaterialProgress> {
    TrainingMaterialProgress selectOne(Long trainingId, Long materialId, Long studentId);
    List<TrainingMaterialProgress> selectList(Wrapper<TrainingMaterialProgress> wrapper);
    int upsert(TrainingMaterialProgress progress);
}


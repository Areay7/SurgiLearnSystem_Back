package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.TrainingMaterial;

import java.util.List;

public interface ITrainingMaterialService extends IService<TrainingMaterial> {
    List<TrainingMaterial> listByTrainingId(Long trainingId);
    int replaceTrainingMaterials(Long trainingId, List<TrainingMaterial> items);
}


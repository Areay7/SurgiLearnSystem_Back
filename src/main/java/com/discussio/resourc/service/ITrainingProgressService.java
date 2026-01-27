package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.TrainingProgress;

import java.util.List;

public interface ITrainingProgressService extends IService<TrainingProgress> {
    TrainingProgress selectByTrainingAndStudent(Long trainingId, Long studentId);
    List<TrainingProgress> selectTrainingProgressList(Wrapper<TrainingProgress> wrapper);
    int upsert(TrainingProgress progress);
}


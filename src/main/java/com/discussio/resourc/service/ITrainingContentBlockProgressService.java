package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.TrainingContentBlockProgress;

import java.util.List;

public interface ITrainingContentBlockProgressService extends IService<TrainingContentBlockProgress> {
    TrainingContentBlockProgress selectByBlockAndStudent(Long blockId, Long studentId);
    List<TrainingContentBlockProgress> listByTrainingAndStudent(Long trainingId, Long studentId);
    int upsert(TrainingContentBlockProgress progress);
}

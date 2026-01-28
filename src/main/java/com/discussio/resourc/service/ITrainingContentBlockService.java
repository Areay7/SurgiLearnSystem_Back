package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.TrainingContentBlock;

import java.util.List;

public interface ITrainingContentBlockService extends IService<TrainingContentBlock> {
    List<TrainingContentBlock> listByTrainingId(Long trainingId);
    int replaceBlocks(Long trainingId, List<TrainingContentBlock> items);
}

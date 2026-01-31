package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Training;

import java.util.List;

/**
 * 护理培训 Service 接口
 */
public interface ITrainingService extends IService<Training> {

    Training selectTrainingById(Long id);

    List<Training> selectTrainingList(Wrapper<Training> queryWrapper);

    List<Training> selectTrainingListForStudent(Long studentId, String searchText, String trainingType, String status);

    int insertTraining(Training training);

    int updateTraining(Training training);

    int deleteTrainingByIds(String ids);

    int deleteTrainingById(Long id);

    List<Long> getTrainingClassIds(Long trainingId);

    void setTrainingClassIds(Long trainingId, List<Long> classIds);

    boolean canStudentAccessTraining(Long trainingId, Long studentId);
}


package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.TrainingClassMapper;
import com.discussio.resourc.mapper.auto.TrainingMapper;
import com.discussio.resourc.model.auto.Training;
import com.discussio.resourc.model.auto.TrainingClass;
import com.discussio.resourc.service.ITrainingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 护理培训 Service 实现
 */
@Service
public class TrainingServiceImpl extends ServiceImpl<TrainingMapper, Training> implements ITrainingService {

    @Autowired
    private TrainingClassMapper trainingClassMapper;

    @Override
    public Training selectTrainingById(Long id) {
        return this.baseMapper.selectTrainingById(id);
    }

    @Override
    public List<Training> selectTrainingList(Wrapper<Training> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertTraining(Training training) {
        if (StringUtils.isBlank(training.getTrainingName())) {
            throw new RuntimeException("培训名称不能为空！");
        }
        if (training.getCreateTime() == null) {
            training.setCreateTime(new Date());
        }
        if (training.getUpdateTime() == null) {
            training.setUpdateTime(new Date());
        }
        if (training.getStatus() == null) {
            training.setStatus("未开始");
        }
        return this.baseMapper.insert(training);
    }

    @Override
    public int updateTraining(Training training) {
        if (training.getId() == null) {
            throw new RuntimeException("培训ID不能为空！");
        }
        training.setUpdateTime(new Date());
        return this.baseMapper.updateById(training);
    }

    @Override
    public int deleteTrainingByIds(String ids) {
        for (String sid : ConvertUtil.toStrArray(ids)) {
            Long id = Long.valueOf(sid);
            trainingClassMapper.delete(new QueryWrapper<TrainingClass>().eq("training_id", id));
        }
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteTrainingById(Long id) {
        if (id == null) {
            throw new RuntimeException("培训ID不能为空！");
        }
        trainingClassMapper.delete(new QueryWrapper<TrainingClass>().eq("training_id", id));
        return this.baseMapper.deleteById(id);
    }

    @Override
    public List<Training> selectTrainingListForStudent(Long studentId, String searchText, String trainingType, String status) {
        return this.baseMapper.selectTrainingListForStudent(studentId, searchText, trainingType, status);
    }

    @Override
    public List<Long> getTrainingClassIds(Long trainingId) {
        List<TrainingClass> list = trainingClassMapper.selectList(new QueryWrapper<TrainingClass>().eq("training_id", trainingId));
        return list.stream().map(TrainingClass::getClassId).collect(Collectors.toList());
    }

    @Override
    public void setTrainingClassIds(Long trainingId, List<Long> classIds) {
        trainingClassMapper.delete(new QueryWrapper<TrainingClass>().eq("training_id", trainingId));
        if (classIds != null && !classIds.isEmpty()) {
            for (Long classId : classIds) {
                if (classId != null) {
                    TrainingClass tc = new TrainingClass();
                    tc.setTrainingId(trainingId);
                    tc.setClassId(classId);
                    tc.setCreateTime(new Date());
                    trainingClassMapper.insert(tc);
                }
            }
        }
    }

    @Override
    public boolean canStudentAccessTraining(Long trainingId, Long studentId) {
        List<Long> classIds = getTrainingClassIds(trainingId);
        if (classIds == null || classIds.isEmpty()) return true;
        List<Training> allowed = baseMapper.selectTrainingListForStudent(studentId, null, null, null);
        return allowed.stream().anyMatch(t -> trainingId.equals(t.getId()));
    }
}


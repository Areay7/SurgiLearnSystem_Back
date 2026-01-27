package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.TrainingProgressMapper;
import com.discussio.resourc.model.auto.TrainingProgress;
import com.discussio.resourc.service.ITrainingProgressService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainingProgressServiceImpl extends ServiceImpl<TrainingProgressMapper, TrainingProgress>
        implements ITrainingProgressService {

    @Override
    public TrainingProgress selectByTrainingAndStudent(Long trainingId, Long studentId) {
        if (trainingId == null || studentId == null) return null;
        return this.baseMapper.selectOne(new QueryWrapper<TrainingProgress>()
                .eq("training_id", trainingId)
                .eq("student_id", studentId)
                .last("limit 1"));
    }

    @Override
    public List<TrainingProgress> selectTrainingProgressList(Wrapper<TrainingProgress> wrapper) {
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public int upsert(TrainingProgress progress) {
        if (progress == null || progress.getTrainingId() == null || progress.getStudentId() == null) {
            throw new RuntimeException("trainingId/studentId 不能为空");
        }
        TrainingProgress old = selectByTrainingAndStudent(progress.getTrainingId(), progress.getStudentId());
        Date now = new Date();
        if (old == null) {
            if (progress.getProgressPercent() == null) progress.setProgressPercent(0);
            if (progress.getCompletedCount() == null) progress.setCompletedCount(0);
            if (progress.getTotalCount() == null) progress.setTotalCount(0);
            if (progress.getStatus() == null) progress.setStatus("学习中");
            progress.setCreateTime(now);
            progress.setUpdateTime(now);
            progress.setLastStudyTime(now);
            return this.baseMapper.insert(progress);
        } else {
            progress.setId(old.getId());
            progress.setUpdateTime(now);
            progress.setLastStudyTime(now);
            return this.baseMapper.updateById(progress);
        }
    }
}


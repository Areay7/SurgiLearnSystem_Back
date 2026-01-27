package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.TrainingMaterialProgressMapper;
import com.discussio.resourc.model.auto.TrainingMaterialProgress;
import com.discussio.resourc.service.ITrainingMaterialProgressService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainingMaterialProgressServiceImpl extends ServiceImpl<TrainingMaterialProgressMapper, TrainingMaterialProgress>
        implements ITrainingMaterialProgressService {

    @Override
    public TrainingMaterialProgress selectOne(Long trainingId, Long materialId, Long studentId) {
        if (trainingId == null || materialId == null || studentId == null) return null;
        return this.baseMapper.selectOne(new QueryWrapper<TrainingMaterialProgress>()
                .eq("training_id", trainingId)
                .eq("material_id", materialId)
                .eq("student_id", studentId)
                .last("limit 1"));
    }

    @Override
    public List<TrainingMaterialProgress> selectList(Wrapper<TrainingMaterialProgress> wrapper) {
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public int upsert(TrainingMaterialProgress progress) {
        if (progress == null || progress.getTrainingId() == null || progress.getMaterialId() == null || progress.getStudentId() == null) {
            throw new RuntimeException("trainingId/materialId/studentId 不能为空");
        }
        TrainingMaterialProgress old = selectOne(progress.getTrainingId(), progress.getMaterialId(), progress.getStudentId());
        Date now = new Date();
        if (old == null) {
            if (progress.getProgressPercent() == null) progress.setProgressPercent(0);
            if (progress.getCompleted() == null) progress.setCompleted(0);
            if (progress.getLastPosition() == null) progress.setLastPosition(0);
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


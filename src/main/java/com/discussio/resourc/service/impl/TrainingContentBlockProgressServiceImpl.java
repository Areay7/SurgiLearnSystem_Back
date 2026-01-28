package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.TrainingContentBlockProgressMapper;
import com.discussio.resourc.model.auto.TrainingContentBlockProgress;
import com.discussio.resourc.service.ITrainingContentBlockProgressService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainingContentBlockProgressServiceImpl extends ServiceImpl<TrainingContentBlockProgressMapper, TrainingContentBlockProgress>
        implements ITrainingContentBlockProgressService {

    @Override
    public TrainingContentBlockProgress selectByBlockAndStudent(Long blockId, Long studentId) {
        if (blockId == null || studentId == null) return null;
        return this.baseMapper.selectOne(new QueryWrapper<TrainingContentBlockProgress>()
                .eq("block_id", blockId)
                .eq("student_id", studentId)
                .last("limit 1"));
    }

    @Override
    public List<TrainingContentBlockProgress> listByTrainingAndStudent(Long trainingId, Long studentId) {
        if (trainingId == null || studentId == null) return java.util.Collections.emptyList();
        return this.baseMapper.selectList(new QueryWrapper<TrainingContentBlockProgress>()
                .eq("training_id", trainingId)
                .eq("student_id", studentId));
    }

    @Override
    public int upsert(TrainingContentBlockProgress progress) {
        if (progress == null || progress.getBlockId() == null || progress.getStudentId() == null) {
            throw new RuntimeException("blockId/studentId 不能为空");
        }
        TrainingContentBlockProgress old = selectByBlockAndStudent(progress.getBlockId(), progress.getStudentId());
        Date now = new Date();
        if (old == null) {
            if (progress.getViewed() == null) progress.setViewed(0);
            if (progress.getViewDuration() == null) progress.setViewDuration(0);
            if (progress.getPlayProgress() == null) progress.setPlayProgress(0);
            if (progress.getScrollProgress() == null) progress.setScrollProgress(0);
            if (progress.getDownloaded() == null) progress.setDownloaded(0);
            progress.setFirstViewTime(now);
            progress.setLastViewTime(now);
            progress.setCreateTime(now);
            progress.setUpdateTime(now);
            return this.baseMapper.insert(progress);
        } else {
            progress.setId(old.getId());
            progress.setLastViewTime(now);
            progress.setUpdateTime(now);
            // 如果首次浏览，设置 firstViewTime
            if (progress.getViewed() != null && progress.getViewed() == 1 && old.getViewed() == 0) {
                progress.setFirstViewTime(now);
            }
            return this.baseMapper.updateById(progress);
        }
    }
}

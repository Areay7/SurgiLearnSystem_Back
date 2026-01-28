package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.TrainingContentBlockMapper;
import com.discussio.resourc.model.auto.TrainingContentBlock;
import com.discussio.resourc.service.ITrainingContentBlockService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainingContentBlockServiceImpl extends ServiceImpl<TrainingContentBlockMapper, TrainingContentBlock>
        implements ITrainingContentBlockService {

    @Override
    public List<TrainingContentBlock> listByTrainingId(Long trainingId) {
        if (trainingId == null) return java.util.Collections.emptyList();
        return this.baseMapper.selectList(new QueryWrapper<TrainingContentBlock>()
                .eq("training_id", trainingId)
                .orderByAsc("sort_order")
                .orderByAsc("id"));
    }

    @Override
    public int replaceBlocks(Long trainingId, List<TrainingContentBlock> items) {
        if (trainingId == null) {
            throw new RuntimeException("trainingId 不能为空");
        }
        this.baseMapper.delete(new QueryWrapper<TrainingContentBlock>().eq("training_id", trainingId));
        if (items == null || items.isEmpty()) return 0;
        Date now = new Date();
        int inserted = 0;
        for (TrainingContentBlock b : items) {
            if (b == null) continue;
            b.setId(null);
            b.setTrainingId(trainingId);
            if (b.getSortOrder() == null) b.setSortOrder(0);
            b.setCreateTime(now);
            b.setUpdateTime(now);
            inserted += this.baseMapper.insert(b);
        }
        return inserted;
    }
}

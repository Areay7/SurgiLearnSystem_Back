package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.TrainingMaterialMapper;
import com.discussio.resourc.model.auto.TrainingMaterial;
import com.discussio.resourc.service.ITrainingMaterialService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TrainingMaterialServiceImpl extends ServiceImpl<TrainingMaterialMapper, TrainingMaterial>
        implements ITrainingMaterialService {

    @Override
    public List<TrainingMaterial> listByTrainingId(Long trainingId) {
        if (trainingId == null) return java.util.Collections.emptyList();
        return this.baseMapper.selectList(new QueryWrapper<TrainingMaterial>()
                .eq("training_id", trainingId)
                .orderByAsc("sort_order")
                .orderByAsc("id"));
    }

    @Override
    public int replaceTrainingMaterials(Long trainingId, List<TrainingMaterial> items) {
        if (trainingId == null) {
            throw new RuntimeException("trainingId 不能为空");
        }
        // 先删除再插入（简单可靠）
        this.baseMapper.delete(new QueryWrapper<TrainingMaterial>().eq("training_id", trainingId));
        if (items == null || items.isEmpty()) return 0;
        Date now = new Date();
        int inserted = 0;
        for (TrainingMaterial tm : items) {
            if (tm == null) continue;
            if (tm.getMaterialId() == null) continue;
            tm.setId(null);
            tm.setTrainingId(trainingId);
            if (tm.getSortOrder() == null) tm.setSortOrder(0);
            if (tm.getRequired() == null) tm.setRequired(1);
            tm.setCreateTime(now);
            inserted += this.baseMapper.insert(tm);
        }
        return inserted;
    }
}


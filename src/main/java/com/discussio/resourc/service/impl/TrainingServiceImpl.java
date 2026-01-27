package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.TrainingMapper;
import com.discussio.resourc.model.auto.Training;
import com.discussio.resourc.service.ITrainingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 护理培训 Service 实现
 */
@Service
public class TrainingServiceImpl extends ServiceImpl<TrainingMapper, Training> implements ITrainingService {

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
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteTrainingById(Long id) {
        if (id == null) {
            throw new RuntimeException("培训ID不能为空！");
        }
        return this.baseMapper.deleteById(id);
    }
}


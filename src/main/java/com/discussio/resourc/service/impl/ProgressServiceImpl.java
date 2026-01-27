package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.ProgressMapper;
import com.discussio.resourc.model.auto.Progress;
import com.discussio.resourc.service.IProgressService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 学习进度 Service 实现
 */
@Service
public class ProgressServiceImpl extends ServiceImpl<ProgressMapper, Progress> implements IProgressService {

    @Override
    public Progress selectProgressById(Long id) {
        return this.baseMapper.selectProgressById(id);
    }

    @Override
    public List<Progress> selectProgressList(Wrapper<Progress> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertProgress(Progress progress) {
        if (progress.getCreateTime() == null) {
            progress.setCreateTime(new Date());
        }
        if (progress.getUpdateTime() == null) {
            progress.setUpdateTime(new Date());
        }
        if (progress.getProgressPercent() == null) {
            progress.setProgressPercent(0);
        }
        return this.baseMapper.insert(progress);
    }

    @Override
    public int updateProgress(Progress progress) {
        if (progress.getId() == null) {
            throw new RuntimeException("进度ID不能为空！");
        }
        progress.setUpdateTime(new Date());
        return this.baseMapper.updateById(progress);
    }

    @Override
    public int deleteProgressByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteProgressById(Long id) {
        if (id == null) {
            throw new RuntimeException("进度ID不能为空！");
        }
        return this.baseMapper.deleteById(id);
    }
}


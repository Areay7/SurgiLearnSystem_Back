package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.FeedbackModuleMapper;
import com.discussio.resourc.model.auto.FeedbackModule;
import com.discussio.resourc.service.IFeedbackModuleService;
import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 反馈评价模块Service业务处理
 */
@Service
public class FeedbackModuleServiceImpl extends ServiceImpl<FeedbackModuleMapper, FeedbackModule>
        implements IFeedbackModuleService {
    
    private static final Logger logger = LoggerFactory.getLogger(FeedbackModuleServiceImpl.class);

    @Override
    public FeedbackModule selectFeedbackModuleById(Long id) {
        return this.baseMapper.selectFeedbackModuleById(id);
    }

    @Override
    public List<FeedbackModule> selectFeedbackModuleList(Wrapper<FeedbackModule> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<FeedbackModule> selectFeedbackModuleList(FeedbackModule feedbackModule) {
        Map<String, Object> map = BeanUtil.beanToMap(feedbackModule, true, true);
        QueryWrapper<FeedbackModule> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(map, false);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertFeedbackModule(FeedbackModule feedbackModule) {
        if (StringUtils.isEmpty(feedbackModule.getModuleName())) {
            throw new RuntimeException("反馈评价模块.模块名称不能为空！");
        } else if (feedbackModule.getModuleName().length() > 190) {
            throw new RuntimeException("反馈评价模块.模块名称长度不能超过190个字符");
        }
        
        if (feedbackModule.getCreateTime() == null) {
            feedbackModule.setCreateTime(new Date());
        }
        if (feedbackModule.getUpdateTime() == null) {
            feedbackModule.setUpdateTime(new Date());
        }
        
        return this.baseMapper.insert(feedbackModule);
    }

    @Override
    public int updateFeedbackModule(FeedbackModule feedbackModule) {
        if (StringUtils.isEmpty(feedbackModule.getModuleName())) {
            throw new RuntimeException("反馈评价模块模块名称不能为空！");
        }
        feedbackModule.setUpdateTime(new Date());
        return this.baseMapper.updateById(feedbackModule);
    }

    @Override
    public int deleteFeedbackModuleByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteFeedbackModuleById(Long id) {
        if (id == null) {
            throw new RuntimeException("反馈评价模块id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int updateFeedbackModuleVisible(FeedbackModule feedbackModule) {
        return this.baseMapper.updateById(feedbackModule);
    }

    @Override
    public List<FeedbackModule> getFeedbackModuleListByModuleType(String moduleType) {
        if (moduleType == null) {
            throw new RuntimeException("反馈评价模块模块类型不能为空");
        }
        return this.baseMapper.selectFeedbackModuleListByModuleType(moduleType);
    }
}

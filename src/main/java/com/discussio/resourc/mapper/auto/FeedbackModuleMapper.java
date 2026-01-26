package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.FeedbackModule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 反馈评价模块Mapper接口
 */
@Mapper
public interface FeedbackModuleMapper extends BaseMapper<FeedbackModule> {
    FeedbackModule selectFeedbackModuleById(Long id);
    List<FeedbackModule> selectFeedbackModuleListByModuleType(String moduleType);
}

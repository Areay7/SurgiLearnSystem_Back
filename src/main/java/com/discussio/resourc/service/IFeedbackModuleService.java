package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.FeedbackModule;

import java.util.List;

/**
 * 反馈评价模块Service接口
 */
public interface IFeedbackModuleService extends IService<FeedbackModule> {
    FeedbackModule selectFeedbackModuleById(Long id);
    List<FeedbackModule> selectFeedbackModuleList(Wrapper<FeedbackModule> queryWrapper);
    List<FeedbackModule> selectFeedbackModuleList(FeedbackModule feedbackModule);
    int insertFeedbackModule(FeedbackModule feedbackModule);
    int updateFeedbackModule(FeedbackModule feedbackModule);
    int deleteFeedbackModuleByIds(String ids);
    int deleteFeedbackModuleById(Long id);
    int updateFeedbackModuleVisible(FeedbackModule feedbackModule);
    List<FeedbackModule> getFeedbackModuleListByModuleType(String moduleType);
}

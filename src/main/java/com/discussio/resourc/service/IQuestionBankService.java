package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.QuestionBank;

import java.util.List;

/**
 * 在线题库Service接口
 */
public interface IQuestionBankService extends IService<QuestionBank> {
    QuestionBank selectQuestionBankById(Long id);
    List<QuestionBank> selectQuestionBankList(Wrapper<QuestionBank> queryWrapper);
    int insertQuestionBank(QuestionBank questionBank);
    int updateQuestionBank(QuestionBank questionBank);
    int deleteQuestionBankByIds(String ids);
    int deleteQuestionBankById(Long id);
}

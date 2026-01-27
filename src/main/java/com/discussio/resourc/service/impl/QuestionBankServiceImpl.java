package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.QuestionBankMapper;
import com.discussio.resourc.model.auto.QuestionBank;
import com.discussio.resourc.service.IQuestionBankService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 在线题库Service业务处理
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
        implements IQuestionBankService {
    
    private static final Logger logger = LoggerFactory.getLogger(QuestionBankServiceImpl.class);

    @Override
    public QuestionBank selectQuestionBankById(Long id) {
        return this.baseMapper.selectQuestionBankById(id);
    }

    @Override
    public List<QuestionBank> selectQuestionBankList(Wrapper<QuestionBank> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertQuestionBank(QuestionBank questionBank) {
        if (StringUtils.isEmpty(questionBank.getQuestionContent())) {
            throw new RuntimeException("题目内容不能为空！");
        }
        if (StringUtils.isEmpty(questionBank.getQuestionType())) {
            throw new RuntimeException("题目类型不能为空！");
        }
        if (StringUtils.isEmpty(questionBank.getCorrectAnswer())) {
            throw new RuntimeException("正确答案不能为空！");
        }
        
        // 验证题目类型和答案的匹配
        validateQuestion(questionBank);
        
        if (questionBank.getCreateTime() == null) {
            questionBank.setCreateTime(new Date());
        }
        if (questionBank.getUpdateTime() == null) {
            questionBank.setUpdateTime(new Date());
        }
        if (questionBank.getScore() == null) {
            questionBank.setScore(0);
        }
        return this.baseMapper.insert(questionBank);
    }

    @Override
    public int updateQuestionBank(QuestionBank questionBank) {
        if (questionBank.getId() == null) {
            throw new RuntimeException("题目ID不能为空！");
        }
        
        // 验证题目类型和答案的匹配
        validateQuestion(questionBank);
        
        questionBank.setUpdateTime(new Date());
        return this.baseMapper.updateById(questionBank);
    }

    @Override
    public int deleteQuestionBankByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteQuestionBankById(Long id) {
        if (id == null) {
            throw new RuntimeException("题目ID不能为空");
        }
        return this.baseMapper.deleteById(id);
    }
    
    /**
     * 验证题目类型和答案的匹配
     */
    private void validateQuestion(QuestionBank questionBank) {
        String questionType = questionBank.getQuestionType();
        String correctAnswer = questionBank.getCorrectAnswer();
        
        if (StringUtils.isEmpty(questionType) || StringUtils.isEmpty(correctAnswer)) {
            return; // 已经在 insert/update 中验证了非空
        }
        
        if ("单选".equals(questionType)) {
            // 单选题：答案应该是单个选项 A、B、C、D 之一
            if (!correctAnswer.matches("^[A-D]$")) {
                throw new RuntimeException("单选题的正确答案必须是 A、B、C、D 中的一个");
            }
            // 单选题至少需要两个选项
            if (StringUtils.isEmpty(questionBank.getOptionA()) || StringUtils.isEmpty(questionBank.getOptionB())) {
                throw new RuntimeException("单选题至少需要选项A和选项B");
            }
        } else if ("多选".equals(questionType)) {
            // 多选题：答案应该是多个选项，用逗号分隔，如 A,B,C
            if (!correctAnswer.matches("^[A-D](,[A-D])*$")) {
                throw new RuntimeException("多选题的正确答案格式不正确，应为 A、B、C、D 的组合，用逗号分隔");
            }
            // 多选题至少需要两个选项
            if (StringUtils.isEmpty(questionBank.getOptionA()) || StringUtils.isEmpty(questionBank.getOptionB())) {
                throw new RuntimeException("多选题至少需要选项A和选项B");
            }
        } else if ("判断".equals(questionType)) {
            // 判断题：答案应该是 "正确" 或 "错误"
            if (!"正确".equals(correctAnswer) && !"错误".equals(correctAnswer)) {
                throw new RuntimeException("判断题的正确答案必须是 '正确' 或 '错误'");
            }
            // 判断题只需要选项A（正确）和选项B（错误）
            if (StringUtils.isEmpty(questionBank.getOptionA()) || StringUtils.isEmpty(questionBank.getOptionB())) {
                throw new RuntimeException("判断题需要选项A（正确）和选项B（错误）");
            }
        } else {
            throw new RuntimeException("不支持的题目类型：" + questionType + "，支持的类型：单选、多选、判断");
        }
    }
}

package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.QuestionBank;
import org.apache.ibatis.annotations.Mapper;

/**
 * 在线题库Mapper接口
 */
@Mapper
public interface QuestionBankMapper extends BaseMapper<QuestionBank> {
    QuestionBank selectQuestionBankById(Long id);
}

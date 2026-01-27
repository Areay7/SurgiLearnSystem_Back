package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.ExamResult;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试结果Mapper接口
 */
@Mapper
public interface ExamResultMapper extends BaseMapper<ExamResult> {
    ExamResult selectExamResultById(Long id);
}

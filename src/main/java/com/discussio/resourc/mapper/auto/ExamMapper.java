package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.Exam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考试系统Mapper接口
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
    Exam selectExamById(Long id);
}

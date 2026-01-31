package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 考试系统Mapper接口
 */
@Mapper
public interface ExamMapper extends BaseMapper<Exam> {
    Exam selectExamById(Long id);

    List<Exam> selectExamListForStudent(@Param("studentId") Long studentId,
                                        @Param("searchText") String searchText,
                                        @Param("examType") String examType,
                                        @Param("status") String status,
                                        @Param("examDate") Date examDate);
}

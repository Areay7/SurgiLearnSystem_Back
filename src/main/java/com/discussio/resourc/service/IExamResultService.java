package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.ExamResult;

import java.util.List;

/**
 * 考试结果Service接口
 */
public interface IExamResultService extends IService<ExamResult> {
    ExamResult selectExamResultById(Long id);
    List<ExamResult> selectExamResultList(Wrapper<ExamResult> queryWrapper);
    ExamResult selectExamResultByExamIdAndStudentId(Long examId, String studentId);
    int insertExamResult(ExamResult examResult);
    int updateExamResult(ExamResult examResult);
    int deleteExamResultByIds(String ids);
    int deleteExamResultById(Long id);
}

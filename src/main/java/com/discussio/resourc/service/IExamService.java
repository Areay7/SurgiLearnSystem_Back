package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Exam;

import java.util.Date;
import java.util.List;

/**
 * 考试系统Service接口
 */
public interface IExamService extends IService<Exam> {
    Exam selectExamById(Long id);
    List<Exam> selectExamList(Wrapper<Exam> queryWrapper);
    List<Exam> selectExamListForStudent(Long studentId, String searchText, String examType, String status, Date examDate);
    int insertExam(Exam exam);
    int updateExam(Exam exam);
    int deleteExamByIds(String ids);
    int deleteExamById(Long id);
    List<Long> getExamClassIds(Long examId);
    void setExamClassIds(Long examId, List<Long> classIds);
    boolean canStudentAccessExam(Long examId, Long studentId);
}

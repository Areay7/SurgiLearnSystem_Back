package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Exam;

import java.util.List;

/**
 * 考试系统Service接口
 */
public interface IExamService extends IService<Exam> {
    Exam selectExamById(Long id);
    List<Exam> selectExamList(Wrapper<Exam> queryWrapper);
    int insertExam(Exam exam);
    int updateExam(Exam exam);
    int deleteExamByIds(String ids);
    int deleteExamById(Long id);
}

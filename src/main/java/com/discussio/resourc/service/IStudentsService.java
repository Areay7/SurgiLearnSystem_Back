package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Students;

import java.util.List;

/**
 * 学员记录管理Service接口
 */
public interface IStudentsService extends IService<Students> {
    Students selectStudentsById(Long id);
    List<Students> selectStudentsList(Wrapper<Students> queryWrapper);
    int insertStudents(Students students);
    int updateStudents(Students students);
    int deleteStudentsByIds(String ids);
    int deleteStudentsById(Long id);
}

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
    /**
     * 根据手机号查询学员记录
     */
    Students selectStudentsByPhone(String phone);
    /**
     * 根据手机号更新学员记录
     */
    int updateStudentsByPhone(String phone, Students students);
}

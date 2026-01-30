package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.model.auto.TeachingClass;

import java.util.List;

public interface ITeachingClassService extends IService<TeachingClass> {
    TeachingClass selectById(Long id);
    List<TeachingClass> selectList(Wrapper<TeachingClass> qw);
    int insert(TeachingClass item);
    int update(TeachingClass item);
    int deleteByIds(String ids);

    List<Students> listInstructors(Long classId);
    List<Students> listStudents(Long classId);

    int batchAddInstructors(Long classId, List<Long> studentIds);
    int batchRemoveInstructors(Long classId, List<Long> studentIds);

    int batchAddStudents(Long classId, List<Long> studentIds);
    int batchRemoveStudents(Long classId, List<Long> studentIds);
}


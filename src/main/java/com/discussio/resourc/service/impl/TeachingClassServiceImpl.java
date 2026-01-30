package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.TeachingClassInstructorMapper;
import com.discussio.resourc.mapper.auto.TeachingClassMapper;
import com.discussio.resourc.mapper.auto.TeachingClassStudentMapper;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.model.auto.TeachingClass;
import com.discussio.resourc.model.auto.TeachingClassInstructor;
import com.discussio.resourc.model.auto.TeachingClassStudent;
import com.discussio.resourc.service.ITeachingClassService;
import com.discussio.resourc.service.IStudentsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeachingClassServiceImpl extends ServiceImpl<TeachingClassMapper, TeachingClass> implements ITeachingClassService {

    private final TeachingClassInstructorMapper instructorMapper;
    private final TeachingClassStudentMapper studentMapper;
    private final IStudentsService studentsService;

    public TeachingClassServiceImpl(TeachingClassInstructorMapper instructorMapper,
                                   TeachingClassStudentMapper studentMapper,
                                   IStudentsService studentsService) {
        this.instructorMapper = instructorMapper;
        this.studentMapper = studentMapper;
        this.studentsService = studentsService;
    }

    @Override
    public TeachingClass selectById(Long id) {
        return this.baseMapper.selectByIdSimple(id);
    }

    @Override
    public List<TeachingClass> selectList(Wrapper<TeachingClass> qw) {
        return this.baseMapper.selectList(qw);
    }

    @Override
    public int insert(TeachingClass item) {
        if (item == null || StringUtils.isBlank(item.getClassName())) {
            throw new RuntimeException("班级名称不能为空！");
        }
        if (item.getCreateTime() == null) item.setCreateTime(new Date());
        if (item.getUpdateTime() == null) item.setUpdateTime(new Date());
        if (StringUtils.isBlank(item.getStatus())) item.setStatus("正常");
        return this.baseMapper.insert(item);
    }

    @Override
    public int update(TeachingClass item) {
        if (item == null || item.getId() == null) throw new RuntimeException("班级ID不能为空！");
        item.setUpdateTime(new Date());
        return this.baseMapper.updateById(item);
    }

    @Override
    public int deleteByIds(String ids) {
        List<String> arr = Arrays.asList(ConvertUtil.toStrArray(ids));
        // 先删关联
        for (String sid : arr) {
            Long classId = Long.valueOf(sid);
            instructorMapper.delete(new QueryWrapper<TeachingClassInstructor>().eq("class_id", classId));
            studentMapper.delete(new QueryWrapper<TeachingClassStudent>().eq("class_id", classId));
        }
        return this.baseMapper.deleteBatchIds(arr);
    }

    @Override
    public List<Students> listInstructors(Long classId) {
        List<TeachingClassInstructor> rel = instructorMapper.selectList(new QueryWrapper<TeachingClassInstructor>().eq("class_id", classId));
        return mapStudentsByRel(rel.stream().map(TeachingClassInstructor::getStudentId).collect(Collectors.toList()));
    }

    @Override
    public List<Students> listStudents(Long classId) {
        List<TeachingClassStudent> rel = studentMapper.selectList(new QueryWrapper<TeachingClassStudent>().eq("class_id", classId));
        return mapStudentsByRel(rel.stream().map(TeachingClassStudent::getStudentId).collect(Collectors.toList()));
    }

    @Override
    public int batchAddInstructors(Long classId, List<Long> studentIds) {
        return batchAddRelInstructor(classId, studentIds);
    }

    @Override
    public int batchRemoveInstructors(Long classId, List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) return 0;
        return instructorMapper.delete(new QueryWrapper<TeachingClassInstructor>()
                .eq("class_id", classId)
                .in("student_id", studentIds));
    }

    @Override
    public int batchAddStudents(Long classId, List<Long> studentIds) {
        return batchAddRelStudent(classId, studentIds);
    }

    @Override
    public int batchRemoveStudents(Long classId, List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) return 0;
        return studentMapper.delete(new QueryWrapper<TeachingClassStudent>()
                .eq("class_id", classId)
                .in("student_id", studentIds));
    }

    private int batchAddRelInstructor(Long classId, List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) return 0;
        Date now = new Date();
        int cnt = 0;
        for (Long sid : studentIds) {
            if (sid == null) continue;
            TeachingClassInstructor rel = new TeachingClassInstructor();
            rel.setClassId(classId);
            rel.setStudentId(sid);
            rel.setCreateTime(now);
            try {
                cnt += instructorMapper.insert(rel);
            } catch (Exception ignore) {
                // ignore duplicates
            }
        }
        return cnt;
    }

    private int batchAddRelStudent(Long classId, List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) return 0;
        Date now = new Date();
        int cnt = 0;
        for (Long sid : studentIds) {
            if (sid == null) continue;
            TeachingClassStudent rel = new TeachingClassStudent();
            rel.setClassId(classId);
            rel.setStudentId(sid);
            rel.setCreateTime(now);
            try {
                cnt += studentMapper.insert(rel);
            } catch (Exception ignore) {
                // ignore duplicates
            }
        }
        return cnt;
    }

    private List<Students> mapStudentsByRel(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        List<Students> list = studentsService.listByIds(ids);
        return list != null ? list : Collections.emptyList();
    }
}


package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.StudentsMapper;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.IStudentsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 学员记录管理Service业务处理
 */
@Service
public class StudentsServiceImpl extends ServiceImpl<StudentsMapper, Students>
        implements IStudentsService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentsServiceImpl.class);

    @Override
    public Students selectStudentsById(Long id) {
        return this.baseMapper.selectStudentsById(id);
    }

    @Override
    public List<Students> selectStudentsList(Wrapper<Students> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertStudents(Students students) {
        if (StringUtils.isEmpty(students.getStudentName())) {
            throw new RuntimeException("学员姓名不能为空！");
        }
        if (students.getCreateTime() == null) {
            students.setCreateTime(new Date());
        }
        if (students.getUpdateTime() == null) {
            students.setUpdateTime(new Date());
        }
        return this.baseMapper.insert(students);
    }

    @Override
    public int updateStudents(Students students) {
        students.setUpdateTime(new Date());
        return this.baseMapper.updateById(students);
    }

    @Override
    public int deleteStudentsByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteStudentsById(Long id) {
        if (id == null) {
            throw new RuntimeException("学员id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }
}

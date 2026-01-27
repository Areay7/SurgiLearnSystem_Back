package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.StudentsMapper;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.service.IStudentsService;
import com.discussio.resourc.service.LoginDiscussionForumService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    
    @Autowired(required = false)
    @Lazy
    private LoginDiscussionForumService loginDiscussionForumService;

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
        // 如果更新了 user_type，需要同步更新 login_discussion_forum 表
        if (students.getId() != null && students.getUserType() != null) {
            try {
                // 查询旧的学员记录
                Students oldStudent = this.baseMapper.selectById(students.getId());
                if (oldStudent != null && oldStudent.getPhone() != null) {
                    // 检查 user_type 是否真的发生了变化
                    Integer oldUserType = oldStudent.getUserType();
                    Integer newUserType = students.getUserType();
                    
                    // 只有当 user_type 发生变化时才同步更新
                    if (oldUserType == null || !oldUserType.equals(newUserType)) {
                        // 查询对应的登录用户
                        LoginDiscussionForum loginUser = loginDiscussionForumService != null 
                            ? loginDiscussionForumService.getUserInfo(oldStudent.getPhone()) 
                            : null;
                        
                        if (loginUser != null) {
                            // 映射 students.user_type 到 login_discussion_forum.user_type
                            // students.user_type: 1=学员, 2=讲师, 3=管理员
                            // login_discussion_forum.user_type: 0=普通用户, 1=管理员
                            Integer newLoginUserType = null;
                            if (newUserType == 3) {
                                // 管理员
                                newLoginUserType = 1;
                            } else if (newUserType == 1 || newUserType == 2) {
                                // 学员或讲师 -> 普通用户
                                newLoginUserType = 0;
                            }
                            
                            // 同步更新 login_discussion_forum 表
                            if (newLoginUserType != null && 
                                (loginUser.getUserType() == null || !loginUser.getUserType().equals(newLoginUserType))) {
                                loginUser.setUserType(newLoginUserType);
                                loginDiscussionForumService.updateById(loginUser);
                                logger.info("同步更新登录用户类型 - 手机号: {}, students.user_type: {} -> {}, login_discussion_forum.user_type: {}", 
                                    oldStudent.getPhone(), oldUserType, newUserType, newLoginUserType);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("同步更新登录用户类型失败 - 学员ID: {}, 错误: {}", students.getId(), e.getMessage());
                // 不影响主更新流程，只记录警告
            }
        }
        
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

    @Override
    public Students selectStudentsByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone.trim());
        List<Students> list = this.baseMapper.selectList(queryWrapper);
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    @Override
    public int updateStudentsByPhone(String phone, Students students) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new RuntimeException("手机号不能为空");
        }
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone.trim());
        students.setUpdateTime(new Date());
        return this.baseMapper.update(students, queryWrapper);
    }
}

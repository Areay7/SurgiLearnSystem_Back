package com.discussio.resourc.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.UserPermissionMapper;
import com.discussio.resourc.model.auto.UserPermission;
import com.discussio.resourc.service.IUserPermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 用户权限管理Service业务处理
 */
@Service
public class UserPermissionServiceImpl extends ServiceImpl<UserPermissionMapper, UserPermission>
        implements IUserPermissionService {

    @Override
    public UserPermission selectUserPermissionById(Long id) {
        return this.baseMapper.selectUserPermissionById(id);
    }

    @Override
    public List<UserPermission> selectUserPermissionList(Wrapper<UserPermission> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<UserPermission> selectUserPermissionList(UserPermission userPermission) {
        Map<String, Object> map = BeanUtil.beanToMap(userPermission, true, true);
        QueryWrapper<UserPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(map, false);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertUserPermission(UserPermission userPermission) {
        if (userPermission.getUserId() == null && (userPermission.getUserPhone() == null || userPermission.getUserPhone().trim().isEmpty())) {
            throw new RuntimeException("用户权限管理.用户ID或手机号不能为空！");
        }
        if (StringUtils.isBlank(userPermission.getPermissionCode())) {
            throw new RuntimeException("用户权限管理.权限代码不能为空！");
        }
        if (userPermission.getPermissionCode().length() > 100) {
            throw new RuntimeException("用户权限管理.权限代码长度不能超过100个字符");
        }
        if (StringUtils.isBlank(userPermission.getPermissionName())) {
            throw new RuntimeException("用户权限管理.权限名称不能为空！");
        }
        if (userPermission.getPermissionName().length() > 200) {
            throw new RuntimeException("用户权限管理.权限名称长度不能超过200个字符");
        }
        if (userPermission.getIsActive() == null) {
            userPermission.setIsActive(1);
        }
        if (userPermission.getGrantType() == null || userPermission.getGrantType().trim().isEmpty()) {
            userPermission.setGrantType("grant");
        }
        // 防重复：同一用户+权限+类型已存在则提示
        String phone = userPermission.getUserPhone() != null ? userPermission.getUserPhone().trim() : null;
        Long uid = userPermission.getUserId();
        if (phone != null || uid != null) {
            QueryWrapper<UserPermission> qw = new QueryWrapper<>();
            qw.eq("permission_code", userPermission.getPermissionCode());
            qw.eq("grant_type", userPermission.getGrantType());
            if (uid != null && phone != null) {
                qw.and(w -> w.eq("user_id", uid).or().eq("user_phone", phone));
            } else if (uid != null) {
                qw.eq("user_id", uid);
            } else {
                qw.eq("user_phone", phone);
            }
            if (this.baseMapper.selectCount(qw) > 0) {
                throw new RuntimeException("该用户已存在相同的权限配置（" + userPermission.getPermissionCode() + "），请勿重复添加");
            }
        }
        return this.baseMapper.insert(userPermission);
    }

    @Override
    public int updateUserPermission(UserPermission userPermission) {
        if (userPermission.getId() == null) {
            throw new RuntimeException("用户权限管理.id不能为空！");
        }
        if (userPermission.getUserId() == null && (userPermission.getUserPhone() == null || userPermission.getUserPhone().trim().isEmpty())) {
            throw new RuntimeException("用户权限管理.用户ID或手机号不能为空！");
        }
        if (StringUtils.isBlank(userPermission.getPermissionCode())) {
            throw new RuntimeException("用户权限管理.权限代码不能为空！");
        }
        if (userPermission.getPermissionCode().length() > 100) {
            throw new RuntimeException("用户权限管理.权限代码长度不能超过100个字符");
        }
        if (StringUtils.isBlank(userPermission.getPermissionName())) {
            throw new RuntimeException("用户权限管理.权限名称不能为空！");
        }
        if (userPermission.getPermissionName().length() > 200) {
            throw new RuntimeException("用户权限管理.权限名称长度不能超过200个字符");
        }
        if (userPermission.getIsActive() == null) {
            userPermission.setIsActive(1);
        }
        if (userPermission.getGrantType() == null || userPermission.getGrantType().trim().isEmpty()) {
            userPermission.setGrantType("grant");
        }
        return this.baseMapper.updateById(userPermission);
    }

    @Override
    public int deleteUserPermissionByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteUserPermissionById(Long id) {
        if (id == null) {
            throw new RuntimeException("用户权限管理id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int updateUserPermissionVisible(UserPermission userPermission) {
        return this.baseMapper.updateById(userPermission);
    }
}


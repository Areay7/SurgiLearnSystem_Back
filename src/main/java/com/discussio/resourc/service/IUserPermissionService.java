package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.UserPermission;

import java.util.List;

/**
 * 用户权限管理Service接口
 */
public interface IUserPermissionService extends IService<UserPermission> {
    UserPermission selectUserPermissionById(Long id);

    List<UserPermission> selectUserPermissionList(Wrapper<UserPermission> queryWrapper);

    List<UserPermission> selectUserPermissionList(UserPermission userPermission);

    int insertUserPermission(UserPermission userPermission);

    int updateUserPermission(UserPermission userPermission);

    int deleteUserPermissionByIds(String ids);

    int deleteUserPermissionById(Long id);

    int updateUserPermissionVisible(UserPermission userPermission);
}


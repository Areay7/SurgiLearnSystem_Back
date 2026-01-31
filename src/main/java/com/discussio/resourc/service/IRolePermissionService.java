package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.RolePermission;

import java.util.List;

public interface IRolePermissionService extends IService<RolePermission> {
    List<String> getPermissionCodesByRoleId(Long roleId);
    int setRolePermissions(Long roleId, List<String> permissionCodes);
}

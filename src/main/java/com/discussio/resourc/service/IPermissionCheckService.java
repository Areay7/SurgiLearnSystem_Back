package com.discussio.resourc.service;

import java.util.List;

/**
 * 权限校验服务：根据用户身份（手机号/students.id）判断是否拥有某权限
 */
public interface IPermissionCheckService {
    /**
     * 检查用户是否拥有指定权限
     * @param userPhone 用户手机号（登录标识）
     * @param permissionCode 权限代码
     * @return true 有权限
     */
    boolean hasPermission(String userPhone, String permissionCode);

    /**
     * 获取用户拥有的所有权限代码
     */
    List<String> getUserPermissionCodes(String userPhone);

    /**
     * 根据 userType 获取角色代码：1->student, 2->instructor, 3->admin
     */
    String getRoleCodeByUserType(Integer userType);
}

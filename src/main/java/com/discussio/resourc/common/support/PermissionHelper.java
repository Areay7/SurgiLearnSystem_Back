package com.discussio.resourc.common.support;

import com.discussio.resourc.service.LoginDiscussionForumService;
import com.discussio.resourc.service.IPermissionCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验辅助：从 request 解析当前用户并检查权限
 */
@Component
public class PermissionHelper {

    @Autowired(required = false)
    private LoginDiscussionForumService loginService;

    @Autowired(required = false)
    private IPermissionCheckService permissionCheckService;

    /**
     * 从请求中解析当前登录用户手机号
     */
    public String parseUserPhone(HttpServletRequest request) {
        if (request == null) return null;
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return null;
            String token = auth.startsWith("Bearer ") ? auth.substring(7).trim() : auth.trim();
            return loginService != null ? loginService.parseUsernameFromToken(token) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查当前用户是否拥有指定权限（优先使用权限配置，兼容角色）
     */
    public boolean hasPermission(HttpServletRequest request, String permissionCode) {
        String phone = parseUserPhone(request);
        if (phone == null || phone.trim().isEmpty()) return false;
        if (permissionCheckService == null) return false;
        return permissionCheckService.hasPermission(phone, permissionCode);
    }

    /**
     * 检查是否拥有任一权限
     */
    public boolean hasAnyPermission(HttpServletRequest request, String... permissionCodes) {
        if (permissionCodes == null || permissionCodes.length == 0) return false;
        for (String code : permissionCodes) {
            if (hasPermission(request, code)) return true;
        }
        return false;
    }
}

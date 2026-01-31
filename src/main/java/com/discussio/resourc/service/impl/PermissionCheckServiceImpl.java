package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.model.auto.Role;
import com.discussio.resourc.model.auto.RolePermission;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.model.auto.UserPermission;
import com.discussio.resourc.service.LoginDiscussionForumService;
import com.discussio.resourc.service.IUserPermissionService;
import com.discussio.resourc.mapper.auto.RoleMapper;
import com.discussio.resourc.mapper.auto.RolePermissionMapper;
import com.discussio.resourc.service.IStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限校验：角色权限 + 用户权限覆盖（授予/收回）
 */
@Service
public class PermissionCheckServiceImpl implements com.discussio.resourc.service.IPermissionCheckService {

    @Autowired(required = false)
    private RoleMapper roleMapper;

    @Autowired(required = false)
    private RolePermissionMapper rolePermissionMapper;

    @Autowired(required = false)
    private IUserPermissionService userPermissionService;

    @Autowired(required = false)
    private IStudentsService studentsService;

    @Autowired(required = false)
    private LoginDiscussionForumService loginService;

    @Override
    public boolean hasPermission(String userPhone, String permissionCode) {
        if (userPhone == null || userPhone.trim().isEmpty() || permissionCode == null || permissionCode.trim().isEmpty()) {
            return false;
        }
        List<String> codes = getUserPermissionCodes(userPhone);
        return codes.contains(permissionCode.trim());
    }

    @Override
    public List<String> getUserPermissionCodes(String userPhone) {
        List<String> result = new ArrayList<>();
        if (userPhone == null || userPhone.trim().isEmpty()) return result;

        String phone = userPhone.trim();
        Integer userType = resolveUserType(phone);
        if (userType == null) return result;

        String roleCode = getRoleCodeByUserType(userType);
        if (roleCode == null) return result;

        // 1. 角色默认权限
        Role role = roleMapper != null ? roleMapper.selectOne(new QueryWrapper<Role>().eq("role_code", roleCode).last("limit 1")) : null;
        if (role != null && rolePermissionMapper != null) {
            List<RolePermission> rps = rolePermissionMapper.selectList(new QueryWrapper<RolePermission>().eq("role_id", role.getId()));
            for (RolePermission rp : rps) {
                if (rp.getPermissionCode() != null && !rp.getPermissionCode().isEmpty()) {
                    result.add(rp.getPermissionCode());
                }
            }
        }
        // 若角色权限表为空（未执行 init_permission_data），则按 userType 回退到默认权限
        if (result.isEmpty() && userType != null) {
            if (userType == 3) {
                java.util.Collections.addAll(result, "training:create", "training:edit", "training:delete", "training:materials", "training:progress",
                    "exam:create", "exam:edit", "exam:delete", "exam:records", "schedule:create", "schedule:edit", "schedule:delete",
                    "video:upload", "video:delete", "class:view", "class:create", "class:edit", "class:delete", "class:students", "class:instructors",
                    "permission:manage", "user:view", "user:resetPwd");
            } else if (userType == 2) {
                java.util.Collections.addAll(result, "training:create", "training:edit", "training:materials", "training:progress",
                    "exam:create", "exam:edit", "exam:records", "schedule:create", "schedule:edit", "schedule:delete",
                    "video:upload", "class:view", "class:students", "class:instructors");
            } else if (userType == 1) {
                java.util.Collections.addAll(result, "training:view", "exam:view", "exam:take", "video:view", "video:favorite",
                    "material:view", "resource:view", "schedule:view", "forum:view", "feedback:submit", "certificate:view");
            }
        }

        // 2. 用户权限覆盖
        Students student = studentsService != null ? studentsService.selectStudentsByPhone(phone) : null;
        Long userId = student != null ? student.getId() : null;

        if (userPermissionService != null) {
            QueryWrapper<UserPermission> qw = new QueryWrapper<>();
            if (userId != null) {
                qw.eq("user_id", userId);
            } else {
                qw.eq("user_phone", phone);
            }
            List<UserPermission> ups = userPermissionService.list(qw);
            for (UserPermission up : ups) {
                if (up.getIsActive() != null && up.getIsActive() == 0) continue;
                String code = up.getPermissionCode();
                if (code == null || code.isEmpty()) continue;
                String grantType = up.getGrantType() != null ? up.getGrantType() : "grant";
                if ("revoke".equalsIgnoreCase(grantType)) {
                    result.remove(code);
                } else {
                    if (!result.contains(code)) result.add(code);
                }
            }
        }

        return result;
    }

    @Override
    public String getRoleCodeByUserType(Integer userType) {
        if (userType == null) return null;
        if (userType == 3) return "admin";   // students.user_type 3 = 管理员
        if (userType == 2) return "instructor";
        if (userType == 1) return "student";
        return "student";
    }

    private Integer resolveUserType(String phone) {
        // login_discussion_forum.user_type: 1 = 管理员
        if (loginService != null) {
            LoginDiscussionForum login = loginService.getUserInfo(phone);
            if (login != null && login.getUserType() != null && login.getUserType() == 1) {
                return 3; // 视为管理员
            }
        }
        // students.user_type: 1学员 2讲师 3管理员
        if (studentsService != null) {
            Students s = studentsService.selectStudentsByPhone(phone);
            if (s != null && s.getUserType() != null) {
                return s.getUserType();
            }
        }
        return 1; // 默认学员
    }
}

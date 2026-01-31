package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.RolePermissionMapper;
import com.discussio.resourc.model.auto.RolePermission;
import com.discussio.resourc.service.IRolePermissionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements IRolePermissionService {
    @Override
    public List<String> getPermissionCodesByRoleId(Long roleId) {
        if (roleId == null) return new ArrayList<>();
        List<RolePermission> list = this.list(new QueryWrapper<RolePermission>().eq("role_id", roleId));
        List<String> codes = new ArrayList<>();
        for (RolePermission rp : list) {
            if (rp.getPermissionCode() != null && !rp.getPermissionCode().isEmpty()) {
                codes.add(rp.getPermissionCode());
            }
        }
        return codes;
    }

    @Override
    public int setRolePermissions(Long roleId, List<String> permissionCodes) {
        if (roleId == null) return 0;
        this.remove(new QueryWrapper<RolePermission>().eq("role_id", roleId));
        if (permissionCodes == null || permissionCodes.isEmpty()) return 0;
        int count = 0;
        for (String code : permissionCodes) {
            if (code == null || code.trim().isEmpty()) continue;
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionCode(code.trim());
            if (this.save(rp)) count++;
        }
        return count;
    }
}

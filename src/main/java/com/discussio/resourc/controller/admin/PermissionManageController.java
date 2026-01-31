package com.discussio.resourc.controller.admin;

import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.model.auto.PermissionDef;
import com.discussio.resourc.model.auto.Role;
import com.discussio.resourc.service.IPermissionCheckService;
import com.discussio.resourc.service.IPermissionDefService;
import com.discussio.resourc.service.IRolePermissionService;
import com.discussio.resourc.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 权限管理：权限定义、角色权限、权限校验
 */
@Api(value = "权限管理")
@RestController
@RequestMapping("/PermissionManageController")
@CrossOrigin(origins = "*")
public class PermissionManageController extends BaseController {

    @Autowired
    private IPermissionDefService permissionDefService;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IRolePermissionService rolePermissionService;

    @Autowired(required = false)
    private IPermissionCheckService permissionCheckService;

    @Autowired(required = false)
    private com.discussio.resourc.service.LoginDiscussionForumService loginService;

    @Autowired(required = false)
    private com.discussio.resourc.service.IStudentsService studentsService;

    private boolean isAdmin(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return false;
            String token = auth.startsWith("Bearer ") ? auth.substring(7).trim() : auth.trim();
            String username = loginService != null ? loginService.parseUsernameFromToken(token) : null;
            if (username == null || username.isEmpty()) return false;
            if (loginService != null) {
                com.discussio.resourc.model.auto.LoginDiscussionForum user = loginService.getUserInfo(username);
                if (user != null && user.getUserType() != null && user.getUserType() == 1) return true;
            }
            if (studentsService != null) {
                com.discussio.resourc.model.auto.Students s = studentsService.selectStudentsByPhone(username);
                return s != null && s.getUserType() != null && s.getUserType() == 3;
            }
            return false;
        } catch (Exception e) { return false; }
    }

    @ApiOperation(value = "权限定义列表", notes = "获取所有权限定义，按模块分组")
    @GetMapping("/permissionDef/list")
    public AjaxResult permissionDefList(HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限");
        List<PermissionDef> list = permissionDefService.listAllOrderByModule();
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "角色列表")
    @GetMapping("/role/list")
    public AjaxResult roleList(HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限");
        List<Role> list = roleService.listAll();
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "获取角色权限")
    @GetMapping("/role/{roleId}/permissions")
    public AjaxResult getRolePermissions(@PathVariable Long roleId, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限");
        List<String> codes = rolePermissionService.getPermissionCodesByRoleId(roleId);
        return AjaxResult.success(codes);
    }

    @ApiOperation(value = "设置角色权限")
    @PostMapping("/role/{roleId}/permissions")
    public AjaxResult setRolePermissions(@PathVariable Long roleId, @RequestBody List<String> permissionCodes, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限");
        int n = rolePermissionService.setRolePermissions(roleId, permissionCodes);
        return AjaxResult.success("设置成功", n);
    }

    @ApiOperation(value = "检查用户权限", notes = "调试用")
    @GetMapping("/check")
    public AjaxResult check(@RequestParam String userPhone, @RequestParam String permissionCode, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限");
        if (permissionCheckService == null) return AjaxResult.success(false);
        boolean has = permissionCheckService.hasPermission(userPhone, permissionCode);
        return AjaxResult.success(has);
    }

    @ApiOperation(value = "获取用户权限列表")
    @GetMapping("/user/{userPhone}/permissions")
    public AjaxResult getUserPermissions(@PathVariable String userPhone, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限");
        if (permissionCheckService == null) return AjaxResult.success(java.util.Collections.emptyList());
        List<String> codes = permissionCheckService.getUserPermissionCodes(userPhone);
        return AjaxResult.success(codes);
    }
}

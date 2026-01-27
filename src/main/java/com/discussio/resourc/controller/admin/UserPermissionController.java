package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.UserPermission;
import com.discussio.resourc.service.IUserPermissionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户权限管理 Controller
 */
@Api(value = "用户权限管理")
@RestController
@RequestMapping("/UserPermissionController")
@CrossOrigin(origins = "*")
public class UserPermissionController extends BaseController {

    @Autowired
    private IUserPermissionService userPermissionService;

    @ApiOperation(value = "用户权限管理列表", notes = "用户权限管理列表")
    @GetMapping("/list")
    public ResultTable list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String permissionCode,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) Integer isActive) {

        QueryWrapper<UserPermission> queryWrapper = new QueryWrapper<>();

        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (permissionCode != null && !permissionCode.trim().isEmpty()) {
            queryWrapper.like("permission_code", permissionCode.trim());
        }
        if (permissionName != null && !permissionName.trim().isEmpty()) {
            queryWrapper.like("permission_name", permissionName.trim());
        }
        if (isActive != null) {
            queryWrapper.eq("is_active", isActive);
        }

        // 通用搜索（兼容旧接口）
        if (searchText != null && !searchText.trim().isEmpty()) {
            String st = searchText.trim();
            queryWrapper.and(wrapper -> wrapper
                    .like("permission_code", st)
                    .or()
                    .like("permission_name", st)
            );
        }

        queryWrapper.orderByDesc("id");

        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<UserPermission> list = userPermissionService.selectUserPermissionList(queryWrapper);
        PageInfo<UserPermission> pageInfo = new PageInfo<>(list);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "用户权限管理新增", notes = "用户权限管理新增")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody UserPermission userPermission) {
        return toAjax(userPermissionService.insertUserPermission(userPermission));
    }

    @ApiOperation(value = "用户权限管理删除", notes = "用户权限管理删除")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids) {
        return toAjax(userPermissionService.deleteUserPermissionByIds(ids));
    }

    @ApiOperation(value = "用户权限管理详情", notes = "获取用户权限管理详情")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        return AjaxResult.success(userPermissionService.selectUserPermissionById(id));
    }

    @ApiOperation(value = "用户权限管理修改保存", notes = "用户权限管理修改保存")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody UserPermission userPermission) {
        return toAjax(userPermissionService.updateUserPermission(userPermission));
    }
}


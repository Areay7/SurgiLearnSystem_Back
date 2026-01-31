package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.IStudentsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 学员记录管理 Controller
 */
@Api(value = "学员记录管理")
@RestController
@RequestMapping("/StudentsController")
@CrossOrigin(origins = "*")
public class StudentsController extends BaseController {
    
    @Autowired
    private IStudentsService studentsService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.discussio.resourc.common.support.PermissionHelper permissionHelper;

    @ApiOperation(value = "学员记录管理列表", notes = "需 user:view 权限")
    @GetMapping("/list")
    public ResultTable Studentslist(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String studentName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) Integer userType,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "user:view")) {
            return new com.discussio.resourc.common.domain.ResultTable(403, "无权限查看用户", 0, java.util.Collections.emptyList());
        }
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        
        // 学员姓名搜索
        if (studentName != null && !studentName.trim().isEmpty()) {
            queryWrapper.like("student_name", studentName.trim());
        }
        
        // 手机号搜索
        if (phone != null && !phone.trim().isEmpty()) {
            queryWrapper.like("phone", phone.trim());
        }
        
        // 员工编号搜索
        if (employeeId != null && !employeeId.trim().isEmpty()) {
            queryWrapper.like("employee_id", employeeId.trim());
        }
        
        // 用户类型筛选
        if (userType != null) {
            queryWrapper.eq("user_type", userType);
        }
        
        // 状态筛选
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq("status", status.trim());
        }
        
        // 通用搜索（兼容旧接口）
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("student_name", searchText)
                .or()
                .like("phone", searchText)
                .or()
                .like("employee_id", searchText)
            );
        }
        
        // 排序：按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<Students> list = studentsService.selectStudentsList(queryWrapper);
        PageInfo<Students> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "学员记录管理新增", notes = "需 user:edit 权限")
    @PostMapping("/add")
    public AjaxResult Studentsadd(@RequestBody Students students, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "user:edit")) {
            return AjaxResult.error(403, "无权限新增用户");
        }
        return toAjax(studentsService.insertStudents(students));
    }

    @ApiOperation(value = "学员记录管理删除", notes = "需 user:edit 权限")
    @DeleteMapping("/remove")
    public AjaxResult Studentsremove(@RequestParam String ids, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "user:edit")) {
            return AjaxResult.error(403, "无权限删除用户");
        }
        return toAjax(studentsService.deleteStudentsByIds(ids));
    }

    @ApiOperation(value = "学员记录管理详情", notes = "需 user:view 权限")
    @GetMapping("/detail/{id}")
    public AjaxResult Studentsdetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "user:view")) {
            return AjaxResult.error(403, "无权限查看用户");
        }
        return AjaxResult.success(studentsService.selectStudentsById(id));
    }

    @ApiOperation(value = "学员记录管理修改保存", notes = "需 user:edit 权限")
    @PostMapping("/edit")
    public AjaxResult StudentseditSave(@RequestBody Students students, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "user:edit")) {
            return AjaxResult.error(403, "无权限编辑用户");
        }
        return toAjax(studentsService.updateStudents(students));
    }
}

package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.model.auto.TeachingClass;
import com.discussio.resourc.service.IStudentsService;
import com.discussio.resourc.service.ITeachingClassService;
import com.discussio.resourc.service.LoginDiscussionForumService;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(value = "班级管理")
@RestController
@RequestMapping("/TeachingClassController")
@CrossOrigin(origins = "*")
public class TeachingClassController extends BaseController {

    private final ITeachingClassService classService;
    private final LoginDiscussionForumService loginService;

    @Autowired(required = false)
    private IStudentsService studentsService;

    @Autowired(required = false)
    private com.discussio.resourc.common.support.PermissionHelper permissionHelper;

    public TeachingClassController(ITeachingClassService classService,
                                  LoginDiscussionForumService loginService) {
        this.classService = classService;
        this.loginService = loginService;
    }

    @ApiOperation(value = "班级列表（用于培训/考试指定班级，管理员和讲师可调用）")
    @GetMapping("/listForAssign")
    public ResultTable listForAssign(@RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer limit,
                                     @RequestParam(required = false) String searchText,
                                     HttpServletRequest request) {
        boolean canView = permissionHelper != null ? permissionHelper.hasAnyPermission(request, "class:view", "class:students", "class:instructors") : isAdminOrInstructor(request);
        if (!canView) return pageTable(java.util.Collections.emptyList(), 0);
        QueryWrapper<TeachingClass> qw = new QueryWrapper<>();
        qw.eq("status", "正常");
        if (searchText != null && !searchText.trim().isEmpty()) {
            qw.and(w -> w.like("class_name", searchText).or().like("class_code", searchText));
        }
        qw.orderByDesc("update_time").orderByDesc("id");
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 200);
        List<TeachingClass> list = classService.selectList(qw);
        PageInfo<TeachingClass> p = new PageInfo<>(list);
        return pageTable(p.getList(), p.getTotal());
    }

    @ApiOperation(value = "班级列表（管理员）", notes = "仅管理员可见")
    @GetMapping("/list")
    public ResultTable list(@RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) String searchText,
                            HttpServletRequest request) {
        boolean canView = permissionHelper != null ? permissionHelper.hasPermission(request, "class:view") : isAdmin(request);
        if (!canView) return pageTable(java.util.Collections.emptyList(), 0);
        QueryWrapper<TeachingClass> qw = new QueryWrapper<>();
        if (searchText != null && !searchText.trim().isEmpty()) {
            qw.and(w -> w.like("class_name", searchText).or().like("class_code", searchText));
        }
        qw.orderByDesc("update_time").orderByDesc("id");
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<TeachingClass> list = classService.selectList(qw);
        PageInfo<TeachingClass> p = new PageInfo<>(list);
        return pageTable(p.getList(), p.getTotal());
    }

    @ApiOperation(value = "新增班级", notes = "需 class:create 权限")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody TeachingClass item, HttpServletRequest request) {
        boolean canCreate = permissionHelper != null ? permissionHelper.hasPermission(request, "class:create") : isAdmin(request);
        if (!canCreate) return AjaxResult.error(403, "无权限（需要班级创建权限）");
        return toAjax(classService.insert(item));
    }

    @ApiOperation(value = "编辑班级", notes = "需 class:edit 权限")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody TeachingClass item, HttpServletRequest request) {
        boolean canEdit = permissionHelper != null ? permissionHelper.hasPermission(request, "class:edit") : isAdmin(request);
        if (!canEdit) return AjaxResult.error(403, "无权限（需要班级编辑权限）");
        return toAjax(classService.update(item));
    }

    @ApiOperation(value = "删除班级", notes = "需 class:delete 权限")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids, HttpServletRequest request) {
        boolean canDelete = permissionHelper != null ? permissionHelper.hasPermission(request, "class:delete") : isAdmin(request);
        if (!canDelete) return AjaxResult.error(403, "无权限（需要班级删除权限）");
        return toAjax(classService.deleteByIds(ids));
    }

    @ApiOperation(value = "班级讲师列表", notes = "需 class:instructors 权限")
    @GetMapping("/{classId}/instructors")
    public AjaxResult instructors(@PathVariable Long classId, HttpServletRequest request) {
        boolean canView = permissionHelper != null ? permissionHelper.hasAnyPermission(request, "class:view", "class:instructors") : isAdmin(request);
        if (!canView) return AjaxResult.error(403, "无权限");
        return AjaxResult.success(classService.listInstructors(classId));
    }

    @ApiOperation(value = "班级学员列表", notes = "需 class:students 权限")
    @GetMapping("/{classId}/students")
    public AjaxResult students(@PathVariable Long classId, HttpServletRequest request) {
        boolean canView = permissionHelper != null ? permissionHelper.hasAnyPermission(request, "class:view", "class:students") : isAdmin(request);
        if (!canView) return AjaxResult.error(403, "无权限");
        return AjaxResult.success(classService.listStudents(classId));
    }

    @ApiOperation(value = "批量添加讲师", notes = "需 class:instructors 权限")
    @PostMapping("/{classId}/instructors/batchAdd")
    public AjaxResult batchAddInstructors(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        boolean canManage = permissionHelper != null ? permissionHelper.hasPermission(request, "class:instructors") : isAdmin(request);
        if (!canManage) return AjaxResult.error(403, "无权限");
        return toAjax(classService.batchAddInstructors(classId, req.getIds()));
    }

    @ApiOperation(value = "批量删除讲师", notes = "需 class:instructors 权限")
    @PostMapping("/{classId}/instructors/batchRemove")
    public AjaxResult batchRemoveInstructors(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        boolean canManage = permissionHelper != null ? permissionHelper.hasPermission(request, "class:instructors") : isAdmin(request);
        if (!canManage) return AjaxResult.error(403, "无权限");
        return toAjax(classService.batchRemoveInstructors(classId, req.getIds()));
    }

    @ApiOperation(value = "批量添加学员", notes = "需 class:students 权限")
    @PostMapping("/{classId}/students/batchAdd")
    public AjaxResult batchAddStudents(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        boolean canManage = permissionHelper != null ? permissionHelper.hasPermission(request, "class:students") : isAdmin(request);
        if (!canManage) return AjaxResult.error(403, "无权限");
        return toAjax(classService.batchAddStudents(classId, req.getIds()));
    }

    @ApiOperation(value = "批量删除学员", notes = "需 class:students 权限")
    @PostMapping("/{classId}/students/batchRemove")
    public AjaxResult batchRemoveStudents(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        boolean canManage = permissionHelper != null ? permissionHelper.hasPermission(request, "class:students") : isAdmin(request);
        if (!canManage) return AjaxResult.error(403, "无权限");
        return toAjax(classService.batchRemoveStudents(classId, req.getIds()));
    }

    private boolean isAdmin(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return false;
            String token = auth.startsWith("Bearer ") ? auth.substring("Bearer ".length()).trim() : auth.trim();
            String username = loginService.parseUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) return false;
            LoginDiscussionForum user = loginService.getUserInfo(username);
            if (user != null && user.getUserType() != null && user.getUserType() == 1) return true;
            if (studentsService != null) {
                Students s = studentsService.selectStudentsByPhone(username);
                return s != null && s.getUserType() != null && s.getUserType() == 3;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isAdminOrInstructor(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return false;
            String token = auth.startsWith("Bearer ") ? auth.substring("Bearer ".length()).trim() : auth.trim();
            String username = loginService.parseUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) return false;
            LoginDiscussionForum user = loginService.getUserInfo(username);
            if (user != null && user.getUserType() != null && user.getUserType() == 1) return true;
            if (studentsService != null) {
                Students s = studentsService.selectStudentsByPhone(username);
                return s != null && s.getUserType() != null && (s.getUserType() == 2 || s.getUserType() == 3);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static class IdsReq {
        private List<Long> ids;
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
    }
}


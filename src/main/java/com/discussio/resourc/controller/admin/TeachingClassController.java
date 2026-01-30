package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.model.auto.TeachingClass;
import com.discussio.resourc.service.ITeachingClassService;
import com.discussio.resourc.service.LoginDiscussionForumService;
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

    public TeachingClassController(ITeachingClassService classService,
                                  LoginDiscussionForumService loginService) {
        this.classService = classService;
        this.loginService = loginService;
    }

    @ApiOperation(value = "班级列表（管理员）", notes = "仅管理员可见")
    @GetMapping("/list")
    public ResultTable list(@RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) String searchText,
                            HttpServletRequest request) {
        if (!isAdmin(request)) return pageTable(java.util.Collections.emptyList(), 0);
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

    @ApiOperation(value = "新增班级", notes = "仅管理员")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody TeachingClass item, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return toAjax(classService.insert(item));
    }

    @ApiOperation(value = "编辑班级", notes = "仅管理员")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody TeachingClass item, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return toAjax(classService.update(item));
    }

    @ApiOperation(value = "删除班级", notes = "仅管理员")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return toAjax(classService.deleteByIds(ids));
    }

    @ApiOperation(value = "班级讲师列表", notes = "仅管理员")
    @GetMapping("/{classId}/instructors")
    public AjaxResult instructors(@PathVariable Long classId, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return AjaxResult.success(classService.listInstructors(classId));
    }

    @ApiOperation(value = "班级学员列表", notes = "仅管理员")
    @GetMapping("/{classId}/students")
    public AjaxResult students(@PathVariable Long classId, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return AjaxResult.success(classService.listStudents(classId));
    }

    @ApiOperation(value = "批量添加讲师", notes = "仅管理员")
    @PostMapping("/{classId}/instructors/batchAdd")
    public AjaxResult batchAddInstructors(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return toAjax(classService.batchAddInstructors(classId, req.getIds()));
    }

    @ApiOperation(value = "批量删除讲师", notes = "仅管理员")
    @PostMapping("/{classId}/instructors/batchRemove")
    public AjaxResult batchRemoveInstructors(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return toAjax(classService.batchRemoveInstructors(classId, req.getIds()));
    }

    @ApiOperation(value = "批量添加学员", notes = "仅管理员")
    @PostMapping("/{classId}/students/batchAdd")
    public AjaxResult batchAddStudents(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
        return toAjax(classService.batchAddStudents(classId, req.getIds()));
    }

    @ApiOperation(value = "批量删除学员", notes = "仅管理员")
    @PostMapping("/{classId}/students/batchRemove")
    public AjaxResult batchRemoveStudents(@PathVariable Long classId, @RequestBody IdsReq req, HttpServletRequest request) {
        if (!isAdmin(request)) return AjaxResult.error(403, "无权限（仅管理员可操作）");
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
            return user != null && user.getUserType() != null && user.getUserType() == 1;
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


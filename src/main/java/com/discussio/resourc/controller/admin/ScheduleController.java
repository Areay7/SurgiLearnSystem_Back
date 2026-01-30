package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.model.auto.Schedule;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.IScheduleService;
import com.discussio.resourc.service.IStudentsService;
import com.discussio.resourc.service.LoginDiscussionForumService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 课程安排设置 Controller
 */
@Api(value = "课程安排设置")
@RestController
@RequestMapping("/ScheduleController")
@CrossOrigin(origins = "*")
public class ScheduleController extends BaseController {

    @Autowired
    private IScheduleService scheduleService;

    @Autowired
    private LoginDiscussionForumService loginService;

    @Autowired(required = false)
    private IStudentsService studentsService;

    @ApiOperation(value = "课程安排列表", notes = "所有人可查看")
    @GetMapping("/list")
    public ResultTable list(@RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) String searchText,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String instructorName) {
        // 查询前顺手刷新一次状态，保证页面实时性（定时任务也会刷新）
        try { scheduleService.refreshStatusesNow(); } catch (Exception ignore) {}
        QueryWrapper<Schedule> qw = new QueryWrapper<>();
        if (searchText != null && !searchText.trim().isEmpty()) {
            qw.and(w -> w.like("course_name", searchText)
                    .or().like("instructor_name", searchText)
                    .or().like("classroom", searchText));
        }
        if (status != null && !status.trim().isEmpty()) {
            qw.eq("status", status.trim());
        }
        if (instructorName != null && !instructorName.trim().isEmpty()) {
            qw.like("instructor_name", instructorName.trim());
        }
        qw.orderByDesc("schedule_date").orderByDesc("id");
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<Schedule> list = scheduleService.selectScheduleList(qw);
        PageInfo<Schedule> p = new PageInfo<>(list);
        return pageTable(p.getList(), p.getTotal());
    }

    @ApiOperation(value = "新增课程安排", notes = "仅管理员/讲师可新增")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody Schedule schedule, HttpServletRequest request) {
        if (!canManage(request)) {
            return AjaxResult.error(403, "无权限操作（仅管理员/讲师可新建课程安排）");
        }
        return toAjax(scheduleService.insertSchedule(schedule));
    }

    @ApiOperation(value = "编辑课程安排", notes = "仅管理员/讲师可编辑")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody Schedule schedule, HttpServletRequest request) {
        if (!canManage(request)) {
            return AjaxResult.error(403, "无权限操作（仅管理员/讲师可编辑课程安排）");
        }
        return toAjax(scheduleService.updateSchedule(schedule));
    }

    @ApiOperation(value = "删除课程安排", notes = "仅管理员/讲师可删除")
    @DeleteMapping("/remove")
    public AjaxResult remove(@RequestParam String ids, HttpServletRequest request) {
        if (!canManage(request)) {
            return AjaxResult.error(403, "无权限操作（仅管理员/讲师可删除课程安排）");
        }
        return toAjax(scheduleService.deleteScheduleByIds(ids));
    }

    private boolean canManage(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return false;
            String token = auth.startsWith("Bearer ") ? auth.substring("Bearer ".length()).trim() : auth.trim();
            String username = loginService.parseUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) return false;

            LoginDiscussionForum user = loginService.getUserInfo(username);
            // 0-普通用户 1-管理员
            if (user != null && user.getUserType() != null && user.getUserType() == 1) return true;

            // students.user_type: 1=学员 2=讲师 3=管理员
            if (studentsService != null) {
                Students s = studentsService.selectStudentsByPhone(username);
                return s != null && s.getUserType() != null && s.getUserType() == 2;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}


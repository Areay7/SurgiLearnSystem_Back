package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.Exam;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.IExamService;
import com.discussio.resourc.service.IStudentsService;
import com.discussio.resourc.service.LoginDiscussionForumService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 考试系统 Controller
 */
@Api(value = "考试系统管理")
@RestController
@RequestMapping("/ExamController")
@CrossOrigin(origins = "*")
public class ExamController extends BaseController {
    
    @Autowired
    private IExamService examService;

    @Autowired(required = false)
    private LoginDiscussionForumService loginService;

    @Autowired(required = false)
    private IStudentsService studentsService;

    @ApiOperation(value = "考试列表", notes = "获取考试列表；学员仅看到其所在班级的考试")
    @GetMapping("/list")
    public ResultTable examList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String examDate,
            HttpServletRequest request) {
        ExamUserRole role = resolveExamUserRole(request);
        Date examDateParsed = null;
        if (examDate != null && !examDate.trim().isEmpty()) {
            try {
                examDateParsed = new SimpleDateFormat("yyyy-MM-dd").parse(examDate.trim());
            } catch (Exception ignored) {}
        }
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<Exam> list;
        if (role.student != null && !role.isAdmin && !role.isInstructor) {
            list = examService.selectExamListForStudent(role.student.getId(),
                searchText, examType, status, examDateParsed);
        } else {
            QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
            if (searchText != null && !searchText.trim().isEmpty()) {
                queryWrapper.and(w -> w.like("exam_name", searchText.trim()).or().like("exam_type", searchText.trim()));
            }
            if (examType != null && !examType.trim().isEmpty()) {
                queryWrapper.eq("exam_type", examType.trim());
            }
            if (status != null && !status.trim().isEmpty()) {
                queryWrapper.eq("status", status.trim());
            }
            if (examDateParsed != null) {
                queryWrapper.eq("exam_date", examDateParsed);
            }
            queryWrapper.orderByDesc("exam_date", "start_time");
            list = examService.selectExamList(queryWrapper);
        }
        PageInfo<Exam> pageInfo = new PageInfo<>(list);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "新增考试", notes = "新增考试")
    @PostMapping("/add")
    public AjaxResult examAdd(@RequestBody Exam exam) {
        try {
            int n = examService.insertExam(exam);
            if (n > 0 && exam.getId() != null && exam.getClassIds() != null) {
                examService.setExamClassIds(exam.getId(), exam.getClassIds());
            }
            return toAjax(n);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除考试", notes = "删除考试")
    @DeleteMapping("/remove")
    public AjaxResult examRemove(@RequestParam String ids) {
        try {
            return toAjax(examService.deleteExamByIds(ids));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "考试详情", notes = "获取考试详情")
    @GetMapping("/detail/{id}")
    public AjaxResult examDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        try {
            Exam e = examService.selectExamById(id);
            if (e == null) return AjaxResult.error("考试不存在");
            e.setClassIds(examService.getExamClassIds(id));
            ExamUserRole role = resolveExamUserRole(request);
            if (role.student != null && !role.isAdmin && !role.isInstructor) {
                List<Long> classIds = e.getClassIds();
                if (classIds != null && !classIds.isEmpty()) {
                    List<Long> visible = examService.selectExamListForStudent(role.student.getId(), null, null, null, null)
                        .stream().map(Exam::getId).collect(Collectors.toList());
                    if (!visible.contains(id)) {
                        return AjaxResult.error(403, "您不在该考试的指定班级中，无法查看");
                    }
                }
            }
            return AjaxResult.success(e);
        } catch (Exception ex) {
            return AjaxResult.error(ex.getMessage());
        }
    }

    @ApiOperation(value = "修改考试", notes = "修改考试")
    @PostMapping("/edit")
    public AjaxResult examEdit(@RequestBody Exam exam) {
        try {
            int n = examService.updateExam(exam);
            if (n > 0 && exam.getId() != null) {
                examService.setExamClassIds(exam.getId(), exam.getClassIds());
            }
            return toAjax(n);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private static class ExamUserRole {
        boolean isAdmin;
        boolean isInstructor;
        Students student;
    }

    private ExamUserRole resolveExamUserRole(HttpServletRequest request) {
        ExamUserRole r = new ExamUserRole();
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.trim().isEmpty()) return r;
            String token = auth.startsWith("Bearer ") ? auth.substring(7).trim() : auth.trim();
            String username = loginService != null ? loginService.parseUsernameFromToken(token) : null;
            if (username == null || username.isEmpty()) return r;
            LoginDiscussionForum user = loginService != null ? loginService.getUserInfo(username) : null;
            if (user != null && user.getUserType() != null && user.getUserType() == 1) {
                r.isAdmin = true;
            }
            if (studentsService != null) {
                Students s = studentsService.selectStudentsByPhone(username);
                r.student = s;
                if (s != null && s.getUserType() != null) {
                    if (s.getUserType() == 3) r.isAdmin = true;
                    if (s.getUserType() == 2) r.isInstructor = true;
                }
            }
        } catch (Exception ignored) {}
        return r;
    }
}

package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.ExamResult;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.IExamResultService;
import com.discussio.resourc.service.IExamService;
import com.discussio.resourc.service.IStudentsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 考试结果 Controller
 */
@Api(value = "考试结果管理")
@RestController
@RequestMapping("/ExamResultController")
@CrossOrigin(origins = "*")
public class ExamResultController extends BaseController {
    
    @Autowired
    private IExamResultService examResultService;

    @Autowired(required = false)
    private IExamService examService;

    @Autowired(required = false)
    private IStudentsService studentsService;

    @ApiOperation(value = "考试结果列表", notes = "获取考试结果列表")
    @GetMapping("/list")
    public ResultTable examResultList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String status) {
        QueryWrapper<ExamResult> queryWrapper = new QueryWrapper<>();
        
        if (examId != null) {
            queryWrapper.eq("exam_id", examId);
        }
        if (studentId != null && !studentId.trim().isEmpty()) {
            queryWrapper.eq("student_id", studentId.trim());
        }
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq("status", status.trim());
        }
        
        queryWrapper.orderByDesc("create_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<ExamResult> list = examResultService.selectExamResultList(queryWrapper);
        PageInfo<ExamResult> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "获取考试结果", notes = "根据考试ID和学员ID获取考试结果")
    @GetMapping("/getByExamAndStudent")
    public AjaxResult getExamResultByExamAndStudent(
            @RequestParam Long examId,
            @RequestParam String studentId) {
        try {
            ExamResult result = examResultService.selectExamResultByExamIdAndStudentId(examId, studentId);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "开始考试", notes = "创建考试记录")
    @PostMapping("/start")
    public AjaxResult startExam(@RequestBody ExamResult examResult) {
        try {
            if (examService != null && studentsService != null && examResult.getExamId() != null && examResult.getStudentId() != null) {
                Students s = studentsService.selectStudentsByPhone(examResult.getStudentId().trim());
                if (s != null && s.getId() != null) {
                    if (!examService.canStudentAccessExam(examResult.getExamId(), s.getId())) {
                        return AjaxResult.error(403, "您不在该考试的指定班级中，无法参加");
                    }
                }
            }
            // 检查是否已有记录
            ExamResult existing = examResultService.selectExamResultByExamIdAndStudentId(
                examResult.getExamId(), examResult.getStudentId());
            if (existing != null) {
                return AjaxResult.success(existing);
            }
            // 插入新记录
            int result = examResultService.insertExamResult(examResult);
            if (result > 0) {
                // 重新查询获取完整记录（包括ID和创建时间等）
                ExamResult created = examResultService.selectExamResultByExamIdAndStudentId(
                    examResult.getExamId(), examResult.getStudentId());
                if (created != null) {
                    return AjaxResult.success(created);
                } else {
                    return AjaxResult.error("创建成功但无法获取记录");
                }
            } else {
                return AjaxResult.error("创建考试记录失败");
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "保存答案", notes = "保存考试答案")
    @PostMapping("/saveAnswer")
    public AjaxResult saveAnswer(@RequestBody ExamResult examResult) {
        try {
            if (examResult.getId() == null) {
                return AjaxResult.error("考试记录ID不能为空");
            }
            return AjaxResult.success(examResultService.updateExamResult(examResult));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "提交考试", notes = "提交考试并计算分数")
    @PostMapping("/submit")
    public AjaxResult submitExam(@RequestBody ExamResult examResult) {
        try {
            if (examResult.getId() == null) {
                return AjaxResult.error("考试记录ID不能为空");
            }
            examResult.setStatus("已完成");
            examResult.setSubmitTime(new java.util.Date());
            // 计算用时
            if (examResult.getStartTime() != null && examResult.getSubmitTime() != null) {
                long diff = examResult.getSubmitTime().getTime() - examResult.getStartTime().getTime();
                examResult.setDuration((int) (diff / (1000 * 60))); // 转换为分钟
            }
            return AjaxResult.success(examResultService.updateExamResult(examResult));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "考试结果详情", notes = "获取考试结果详情")
    @GetMapping("/detail/{id}")
    public AjaxResult examResultDetail(@PathVariable("id") Long id) {
        try {
            return AjaxResult.success(examResultService.selectExamResultById(id));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}

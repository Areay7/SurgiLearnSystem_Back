package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.Exam;
import com.discussio.resourc.model.auto.ExamResult;
import com.discussio.resourc.model.auto.QuestionBank;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.IExamResultService;
import com.discussio.resourc.service.IExamService;
import com.discussio.resourc.service.IQuestionBankService;
import com.discussio.resourc.service.IStudentsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @Autowired(required = false)
    private IQuestionBankService questionBankService;

    @Autowired(required = false)
    private com.discussio.resourc.common.support.PermissionHelper permissionHelper;

    @ApiOperation(value = "考试结果列表", notes = "需 exam:records 权限")
    @GetMapping("/list")
    public ResultTable examResultList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String studentKeyword,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "exam:records")) {
            return new com.discussio.resourc.common.domain.ResultTable(403, "无权限查看考试记录", 0, java.util.Collections.emptyList());
        }
        QueryWrapper<ExamResult> queryWrapper = new QueryWrapper<>();
        
        if (examId != null) {
            queryWrapper.eq("exam_id", examId);
        }
        if (studentId != null && !studentId.trim().isEmpty()) {
            queryWrapper.eq("student_id", studentId.trim());
        }
        if (studentKeyword != null && !studentKeyword.trim().isEmpty()) {
            String kw = studentKeyword.trim();
            queryWrapper.and(w -> w.like("student_id", kw).or().like("student_name", kw));
        }
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq("status", status.trim());
        }
        
        queryWrapper.orderByDesc("submit_time").orderByDesc("create_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<ExamResult> list = examResultService.selectExamResultList(queryWrapper);
        PageInfo<ExamResult> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "获取考试结果", notes = "需 exam:records 或 exam:take（仅本人）")
    @GetMapping("/getByExamAndStudent")
    public AjaxResult getExamResultByExamAndStudent(
            @RequestParam Long examId,
            @RequestParam String studentId,
            HttpServletRequest request) {
        if (permissionHelper != null) {
            boolean canRecords = permissionHelper.hasPermission(request, "exam:records");
            boolean canTakeOwn = permissionHelper.hasPermission(request, "exam:take")
                    && studentId != null && studentId.trim().equals(permissionHelper.parseUserPhone(request));
            if (!canRecords && !canTakeOwn) {
                return AjaxResult.error(403, "无权限查看考试记录");
            }
        }
        try {
            ExamResult result = examResultService.selectExamResultByExamIdAndStudentId(examId, studentId);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "开始考试", notes = "需 exam:take 权限")
    @PostMapping("/start")
    public AjaxResult startExam(@RequestBody ExamResult examResult, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "exam:take")) {
            return AjaxResult.error(403, "无权限参加考试");
        }
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

    @ApiOperation(value = "保存答案", notes = "需 exam:take 权限")
    @PostMapping("/saveAnswer")
    public AjaxResult saveAnswer(@RequestBody ExamResult examResult, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "exam:take")) {
            return AjaxResult.error(403, "无权限操作");
        }
        try {
            if (examResult.getId() == null) {
                return AjaxResult.error("考试记录ID不能为空");
            }
            return AjaxResult.success(examResultService.updateAnswersOnly(examResult.getId(),
                    examResult.getAnswers() != null ? examResult.getAnswers() : "{}"));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "提交考试", notes = "需 exam:take 权限")
    @PostMapping("/submit")
    public AjaxResult submitExam(@RequestBody ExamResult examResult, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "exam:take")) {
            return AjaxResult.error(403, "无权限提交考试");
        }
        try {
            if (examResult.getId() == null) {
                return AjaxResult.error("考试记录ID不能为空");
            }
            // 先更新答案（确保提交时答案已保存），再获取完整记录用于计算
            if (examResult.getAnswers() != null && !examResult.getAnswers().trim().isEmpty()) {
                examResultService.updateAnswersOnly(examResult.getId(), examResult.getAnswers());
            }
            ExamResult existing = examResultService.selectExamResultById(examResult.getId());
            if (existing == null) {
                return AjaxResult.error("考试记录不存在");
            }
            // 使用请求中的答案（可能最新），若请求无则用已保存的
            String answersToUse = (examResult.getAnswers() != null && !examResult.getAnswers().trim().isEmpty())
                    ? examResult.getAnswers() : existing.getAnswers();
            existing.setAnswers(answersToUse);
            existing.setStatus("已完成");
            existing.setSubmitTime(new java.util.Date());
            // 计算用时
            if (existing.getStartTime() != null && existing.getSubmitTime() != null) {
                long diff = existing.getSubmitTime().getTime() - existing.getStartTime().getTime();
                existing.setDuration((int) (diff / (1000 * 60)));
            }
            // 自动阅卷
            int obtainedScore = calculateObtainedScore(existing);
            existing.setObtainedScore(obtainedScore);
            examResultService.updateExamResult(existing);
            ExamResult updated = examResultService.selectExamResultById(existing.getId());
            return AjaxResult.success(updated);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /** 根据学员答案与题库正确答案比对，计算得分（单选、多选、判断题） */
    private int calculateObtainedScore(ExamResult examResult) {
        if (examService == null || questionBankService == null) return 0;
        Exam exam = examService.selectExamById(examResult.getExamId());
        if (exam == null || StringUtils.isBlank(exam.getQuestionIds())) return 0;
        String answersJson = examResult.getAnswers();
        if (StringUtils.isBlank(answersJson)) return 0;

        List<Long> questionIds = parseQuestionIds(exam.getQuestionIds());
        if (questionIds.isEmpty()) return 0;
        Map<String, String> userAnswers = parseAnswers(answersJson);

        int examTotal = exam.getTotalScore() != null ? exam.getTotalScore() : 100;
        int scorePerQuestion = examTotal / questionIds.size();
        int remainder = examTotal % questionIds.size();

        int total = 0;
        int idx = 0;
        for (Long qid : questionIds) {
            QuestionBank q = questionBankService.getById(qid);
            if (q == null) continue;
            String correct = q.getCorrectAnswer();
            String user = userAnswers.get(String.valueOf(qid));
            if (user == null || StringUtils.isBlank(user)) continue;
            if (isAnswerCorrect(q.getQuestionType(), correct, user)) {
                int pts = (q.getScore() != null && q.getScore() > 0) ? q.getScore()
                        : (scorePerQuestion + (idx < remainder ? 1 : 0));
                total += pts;
            }
            idx++;
        }
        return total;
    }

    private List<Long> parseQuestionIds(String questionIdsJson) {
        if (questionIdsJson == null || questionIdsJson.trim().isEmpty()) return Collections.emptyList();
        try {
            String trimmed = questionIdsJson.trim();
            if (trimmed.startsWith("[")) {
                com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                List<?> list = om.readValue(trimmed, List.class);
                List<Long> ids = new ArrayList<>();
                for (Object o : list) {
                    if (o instanceof Number) ids.add(((Number) o).longValue());
                    else if (o != null) ids.add(Long.parseLong(o.toString()));
                }
                return ids;
            }
            // 兼容逗号分隔格式 "1,2,3"
            String[] parts = trimmed.split(",");
            List<Long> ids = new ArrayList<>();
            for (String p : parts) {
                String s = p.trim();
                if (!s.isEmpty()) {
                    try { ids.add(Long.parseLong(s)); } catch (NumberFormatException ignored) {}
                }
            }
            return ids;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Map<String, String> parseAnswers(String answersJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            return om.readValue(answersJson, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    /** 判断题答案归一化：A/正确->正确，B/错误->错误 */
    private String normalizeJudgeAnswer(String ans) {
        if (ans == null) return "";
        ans = ans.trim();
        if (ans.equals("A") || ans.equals("正确")) return "正确";
        if (ans.equals("B") || ans.equals("错误")) return "错误";
        return ans;
    }

    private boolean isAnswerCorrect(String questionType, String correct, String user) {
        if (StringUtils.isBlank(correct) || StringUtils.isBlank(user)) return false;
        if ("多选".equals(questionType)) {
            return normalizeMultiAnswer(correct).equals(normalizeMultiAnswer(user));
        }
        if ("判断".equals(questionType)) {
            return normalizeJudgeAnswer(correct).equals(normalizeJudgeAnswer(user));
        }
        return correct.trim().equals(user.trim());
    }

    private String normalizeMultiAnswer(String s) {
        if (s == null) return "";
        String[] parts = s.split(",");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        Arrays.sort(parts);
        return String.join(",", parts);
    }

    @ApiOperation(value = "考试结果详情", notes = "需 exam:records 权限")
    @GetMapping("/detail/{id}")
    public AjaxResult examResultDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "exam:records")) {
            return AjaxResult.error(403, "无权限查看考试记录");
        }
        try {
            return AjaxResult.success(examResultService.selectExamResultById(id));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}

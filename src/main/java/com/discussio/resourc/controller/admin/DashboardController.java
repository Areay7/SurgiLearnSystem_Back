package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.model.auto.*;
import com.discussio.resourc.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页仪表盘 Controller
 */
@Api(value = "首页仪表盘")
@RestController
@RequestMapping("/DashboardController")
@CrossOrigin(origins = "*")
public class DashboardController extends BaseController {

    @Autowired
    private IStudentsService studentsService;
    @Autowired
    private ITrainingService trainingService;
    @Autowired
    private IQuestionBankService questionBankService;
    @Autowired
    private IExamService examService;
    @Autowired
    private IVideosService videosService;
    @Autowired
    private IResourceSharingService resourceSharingService;
    @Autowired
    private IDiscussionForumService discussionForumService;
    @Autowired
    private IExamResultService examResultService;
    @Autowired
    private ICertificateIssueService certificateIssueService;

    @ApiOperation(value = "获取首页统计数据", notes = "学员、课程、题库、考试等数量")
    @GetMapping("/stats")
    public AjaxResult stats() {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("studentCount", studentsService.count());
            data.put("trainingCount", trainingService.count());
            data.put("questionCount", questionBankService.count());
            data.put("videoCount", videosService.count());
            data.put("resourceCount", resourceSharingService.count());

            // 进行中考试：未开始、进行中（可参加的考试）
            QueryWrapper<Exam> examQw = new QueryWrapper<>();
            examQw.in("status", "进行中", "未开始");
            data.put("examOngoingCount", examService.count(examQw));
        } catch (Exception e) {
            data.put("studentCount", 0);
            data.put("trainingCount", 0);
            data.put("questionCount", 0);
            data.put("videoCount", 0);
            data.put("resourceCount", 0);
            data.put("examOngoingCount", 0);
        }
        return AjaxResult.success(data);
    }

    @ApiOperation(value = "获取最近活动", notes = "合并多数据源的最近动态")
    @GetMapping("/activities")
    public AjaxResult activities(@RequestParam(defaultValue = "5") Integer limit) {
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            int maxEach = limit != null && limit > 0 ? Math.min(limit, 20) : 15;

            // 1. 最近学员
            QueryWrapper<Students> sw = new QueryWrapper<>();
            sw.orderByDesc("create_time").last("LIMIT " + maxEach);
            for (Students s : studentsService.list(sw)) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "student");
                m.put("time", s.getCreateTime());
                m.put("title", "新学员 " + (s.getStudentName() != null && !s.getStudentName().isEmpty() ? s.getStudentName() : s.getPhone()) + " 加入系统");
                list.add(m);
            }

            // 2. 最近培训课程
            QueryWrapper<Training> tw = new QueryWrapper<>();
            tw.orderByDesc("create_time").last("LIMIT " + maxEach);
            for (Training t : trainingService.list(tw)) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "training");
                m.put("time", t.getCreateTime() != null ? t.getCreateTime() : t.getUpdateTime());
                m.put("title", "课程《" + (t.getTrainingName() != null ? t.getTrainingName() : "未命名") + "》已发布");
                list.add(m);
            }

            // 3. 最近讨论帖
            QueryWrapper<DiscussionForum> fw = new QueryWrapper<>();
            fw.orderByDesc("post_time").last("LIMIT " + maxEach);
            for (DiscussionForum f : discussionForumService.list(fw)) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "forum");
                m.put("time", f.getPostTime());
                m.put("title", "新帖子《" + (f.getForumTitle() != null ? f.getForumTitle() : "无标题") + "》");
                list.add(m);
            }

            // 4. 最近考试完成
            QueryWrapper<ExamResult> erw = new QueryWrapper<>();
            erw.eq("status", "已完成");
            erw.orderByDesc("submit_time").last("LIMIT " + maxEach);
            for (ExamResult er : examResultService.list(erw)) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "exam");
                m.put("time", er.getSubmitTime() != null ? er.getSubmitTime() : er.getCreateTime());
                m.put("title", "考试《" + (er.getExamName() != null ? er.getExamName() : "未命名") + "》已完成");
                list.add(m);
            }

            // 5. 最近视频
            QueryWrapper<Videos> vw = new QueryWrapper<>();
            vw.and(w -> w.eq("status", "published").or().isNull("status"));
            vw.orderByDesc("publish_time").orderByDesc("create_time").last("LIMIT " + maxEach);
            for (Videos v : videosService.list(vw)) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "video");
                m.put("time", v.getPublishTime() != null ? v.getPublishTime() : v.getCreateTime());
                m.put("title", "新视频《" + (v.getVideoTitle() != null ? v.getVideoTitle() : "未命名") + "》已发布");
                list.add(m);
            }

            // 6. 最近证书颁发
            QueryWrapper<CertificateIssue> cw = new QueryWrapper<>();
            cw.orderByDesc("issue_date").last("LIMIT " + maxEach);
            for (CertificateIssue c : certificateIssueService.list(cw)) {
                Map<String, Object> m = new HashMap<>();
                m.put("type", "certificate");
                m.put("time", c.getIssueDate() != null ? c.getIssueDate() : c.getCreateTime());
                m.put("title", (c.getHolderName() != null ? c.getHolderName() : "学员") + " 获得证书");
                list.add(m);
            }

            // 按时间倒序合并，取前 limit 条
            list.sort((a, b) -> {
                Date da = (Date) a.get("time");
                Date db = (Date) b.get("time");
                if (da == null && db == null) return 0;
                if (da == null) return 1;
                if (db == null) return -1;
                return db.compareTo(da);
            });

            int top = limit != null && limit > 0 ? Math.min(limit, 30) : 15;
            list = list.stream().limit(top).collect(Collectors.toList());

        } catch (Exception e) {
            // 异常时返回空列表
        }

        return AjaxResult.success(list);
    }
}

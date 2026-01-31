package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.Training;
import com.discussio.resourc.model.auto.TrainingMaterial;
import com.discussio.resourc.model.auto.TrainingMaterialProgress;
import com.discussio.resourc.model.auto.TrainingProgress;
import com.discussio.resourc.model.auto.TrainingContentBlock;
import com.discussio.resourc.model.auto.TrainingContentBlockProgress;
import com.discussio.resourc.service.ITrainingMaterialProgressService;
import com.discussio.resourc.service.ITrainingMaterialService;
import com.discussio.resourc.service.ITrainingProgressService;
import com.discussio.resourc.service.ITrainingService;
import com.discussio.resourc.service.IStudentsService;
import com.discussio.resourc.service.ITrainingContentBlockService;
import com.discussio.resourc.service.ITrainingContentBlockProgressService;
import com.discussio.resourc.model.auto.Students;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 护理培训进度（开始/上报/完成判断）
 */
@Api(value = "护理培训进度")
@RestController
@RequestMapping("/TrainingProgressController")
@CrossOrigin(origins = "*")
public class TrainingProgressController extends BaseController {

    @Autowired
    private ITrainingProgressService trainingProgressService;

    @Autowired
    private ITrainingMaterialService trainingMaterialService;

    @Autowired
    private ITrainingMaterialProgressService trainingMaterialProgressService;

    @Autowired
    private ITrainingService trainingService;

    @Autowired
    private IStudentsService studentsService;

    @Autowired
    private ITrainingContentBlockService trainingContentBlockService;

    @Autowired
    private ITrainingContentBlockProgressService trainingContentBlockProgressService;

    @ApiOperation(value = "开始培训（初始化进度）")
    @PostMapping("/start")
    public AjaxResult start(@RequestParam Long trainingId,
                            @RequestParam Long studentId,
                            @RequestParam(required = false) String studentName) {
        try {
            if (!trainingService.canStudentAccessTraining(trainingId, studentId)) {
                return AjaxResult.error(403, "您不在该培训的指定班级中，无法学习");
            }
            // 检查是否有白板内容块
            List<TrainingContentBlock> blocks = trainingContentBlockService.listByTrainingId(trainingId);
            int total = 0;
            if (blocks != null && !blocks.isEmpty()) {
                total = blocks.size();
            } else {
                // 没有内容块，使用资料
                List<TrainingMaterial> materials = trainingMaterialService.listByTrainingId(trainingId);
                total = materials != null ? materials.size() : 0;
            }

            TrainingProgress p = new TrainingProgress();
            p.setTrainingId(trainingId);
            p.setStudentId(studentId);
            p.setStudentName(studentName);
            p.setTotalCount(total);
            p.setCompletedCount(0);
            p.setProgressPercent(0);
            p.setStatus("学习中");
            p.setLastStudyTime(new Date());
            trainingProgressService.upsert(p);

            TrainingProgress created = trainingProgressService.selectByTrainingAndStudent(trainingId, studentId);
            return AjaxResult.success(created);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取培训总进度")
    @GetMapping("/get")
    public AjaxResult get(@RequestParam Long trainingId, @RequestParam Long studentId) {
        TrainingProgress p = trainingProgressService.selectByTrainingAndStudent(trainingId, studentId);
        return AjaxResult.success(p);
    }

    @ApiOperation(value = "上报单个资料进度（并刷新总进度）")
    @PostMapping("/reportMaterial")
    public AjaxResult reportMaterial(@RequestBody TrainingMaterialProgress body) {
        try {
            if (body.getTrainingId() == null || body.getMaterialId() == null || body.getStudentId() == null) {
                return AjaxResult.error("trainingId/materialId/studentId 不能为空");
            }
            // 规范化
            if (body.getProgressPercent() == null) body.setProgressPercent(0);
            if (body.getProgressPercent() < 0) body.setProgressPercent(0);
            if (body.getProgressPercent() > 100) body.setProgressPercent(100);
            if (body.getCompleted() == null) body.setCompleted(body.getProgressPercent() >= 100 ? 1 : 0);
            if (body.getProgressPercent() >= 100) body.setCompleted(1);
            if (body.getLastPosition() == null) body.setLastPosition(0);
            body.setLastStudyTime(new Date());

            trainingMaterialProgressService.upsert(body);

            // 刷新总进度
            refreshTrainingProgress(body.getTrainingId(), body.getStudentId());

            TrainingProgress updated = trainingProgressService.selectByTrainingAndStudent(body.getTrainingId(), body.getStudentId());
            return AjaxResult.success(updated);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "获取该培训的资料进度列表")
    @GetMapping("/materialList")
    public AjaxResult materialList(@RequestParam Long trainingId, @RequestParam Long studentId) {
        List<TrainingMaterialProgress> list = trainingMaterialProgressService.selectList(
                new QueryWrapper<TrainingMaterialProgress>()
                        .eq("training_id", trainingId)
                        .eq("student_id", studentId)
                        .orderByDesc("update_time"));
        return AjaxResult.success(list);
    }

    @ApiOperation(value = "上报内容块浏览进度（并刷新总进度）")
    @PostMapping("/reportBlock")
    public AjaxResult reportBlock(@RequestBody TrainingContentBlockProgress body) {
        try {
            if (body.getTrainingId() == null || body.getBlockId() == null || body.getStudentId() == null) {
                return AjaxResult.error("trainingId/blockId/studentId 不能为空");
            }
            // 获取内容块信息
            TrainingContentBlock block = trainingContentBlockService.getById(body.getBlockId());
            if (block == null) {
                return AjaxResult.error("内容块不存在");
            }
            body.setTrainingId(block.getTrainingId());
            body.setBlockType(block.getBlockType());

            // 根据内容块类型判断是否完成
            boolean completed = false;
            if ("text".equals(block.getBlockType())) {
                // 文字：浏览时长 >= 3秒 或 滚动到底部
                completed = (body.getViewed() != null && body.getViewed() == 1) &&
                        (body.getViewDuration() != null && body.getViewDuration() >= 3);
            } else if ("image".equals(block.getBlockType())) {
                // 图片：点击放大查看过（viewed == 1）
                completed = (body.getViewed() != null && body.getViewed() == 1);
            } else if ("video".equals(block.getBlockType())) {
                // 视频：播放完成（playProgress >= 100）
                completed = (body.getPlayProgress() != null && body.getPlayProgress() >= 100);
            } else if ("pdf".equals(block.getBlockType())) {
                // PDF：浏览完成（scrollProgress >= 100 或 viewed == 1 且浏览超过30秒）
                completed = (body.getScrollProgress() != null && body.getScrollProgress() >= 100) ||
                        (body.getViewed() != null && body.getViewed() == 1 && body.getViewDuration() != null && body.getViewDuration() >= 30);
            } else if ("file".equals(block.getBlockType())) {
                // 文件：点击下载过（downloaded == 1）
                completed = (body.getDownloaded() != null && body.getDownloaded() == 1);
            }

            if (completed) {
                body.setViewed(1);
            }

            trainingContentBlockProgressService.upsert(body);

            // 刷新总进度
            refreshTrainingProgress(block.getTrainingId(), body.getStudentId());

            TrainingProgress updated = trainingProgressService.selectByTrainingAndStudent(block.getTrainingId(), body.getStudentId());
            return AjaxResult.success(updated);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 刷新培训总进度（优先使用内容块进度，否则使用资料进度）
     */
    private void refreshTrainingProgress(Long trainingId, Long studentId) {
        // 检查是否有白板内容块
        List<TrainingContentBlock> blocks = trainingContentBlockService.listByTrainingId(trainingId);
        
        if (blocks != null && !blocks.isEmpty()) {
            // 使用内容块进度计算
            int total = blocks.size();
            int completed = 0;
            for (TrainingContentBlock block : blocks) {
                TrainingContentBlockProgress bp = trainingContentBlockProgressService.selectByBlockAndStudent(block.getId(), studentId);
                if (bp != null) {
                    boolean isCompleted = false;
                    if ("text".equals(block.getBlockType())) {
                        // 文字：浏览时长 >= 3秒
                        isCompleted = (bp.getViewed() != null && bp.getViewed() == 1) &&
                                (bp.getViewDuration() != null && bp.getViewDuration() >= 3);
                    } else if ("image".equals(block.getBlockType())) {
                        // 图片：点击放大查看过
                        isCompleted = (bp.getViewed() != null && bp.getViewed() == 1);
                    } else if ("video".equals(block.getBlockType())) {
                        // 视频：播放完成
                        isCompleted = (bp.getPlayProgress() != null && bp.getPlayProgress() >= 100);
                    } else if ("pdf".equals(block.getBlockType())) {
                        // PDF：浏览完成（滚动到底部或浏览超过30秒）
                        isCompleted = (bp.getScrollProgress() != null && bp.getScrollProgress() >= 100) ||
                                (bp.getViewed() != null && bp.getViewed() == 1 && bp.getViewDuration() != null && bp.getViewDuration() >= 30);
                    } else if ("file".equals(block.getBlockType())) {
                        // 文件：点击下载过
                        isCompleted = (bp.getDownloaded() != null && bp.getDownloaded() == 1);
                    }
                    if (isCompleted) completed++;
                }
            }
            int percent = total <= 0 ? 0 : (int) Math.floor(completed * 100.0 / total);

            TrainingProgress p = new TrainingProgress();
            p.setTrainingId(trainingId);
            p.setStudentId(studentId);
            p.setTotalCount(total);
            p.setCompletedCount(completed);
            p.setProgressPercent(percent);
            p.setStatus((total > 0 && completed >= total) ? "已完成" : "学习中");
            p.setLastStudyTime(new Date());
            trainingProgressService.upsert(p);
        } else {
            // 使用资料进度计算（原有逻辑）
            List<TrainingMaterial> materials = trainingMaterialService.listByTrainingId(trainingId);
            int total = materials != null ? materials.size() : 0;
            int completed = 0;
            if (total > 0) {
                List<TrainingMaterialProgress> mpList = trainingMaterialProgressService.selectList(
                        new QueryWrapper<TrainingMaterialProgress>()
                                .eq("training_id", trainingId)
                                .eq("student_id", studentId));
                if (mpList != null) {
                    for (TrainingMaterialProgress mp : mpList) {
                        if (mp != null && mp.getCompleted() != null && mp.getCompleted() == 1) completed++;
                    }
                }
            }
            int percent = total <= 0 ? 0 : (int) Math.floor(completed * 100.0 / total);

            TrainingProgress p = new TrainingProgress();
            p.setTrainingId(trainingId);
            p.setStudentId(studentId);
            p.setTotalCount(total);
            p.setCompletedCount(completed);
            p.setProgressPercent(percent);
            p.setStatus((total > 0 && completed >= total) ? "已完成" : "学习中");
            p.setLastStudyTime(new Date());
            trainingProgressService.upsert(p);
        }
    }

    @ApiOperation(value = "进度列表（管理查看）")
    @GetMapping("/list")
    public ResultTable list(@RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Long trainingId,
                            @RequestParam(required = false) Long studentId,
                            @RequestParam(required = false) String status,
                            @RequestParam(required = false) String searchText) {
        QueryWrapper<TrainingProgress> qw = new QueryWrapper<>();
        if (trainingId != null) qw.eq("training_id", trainingId);
        if (studentId != null) qw.eq("student_id", studentId);
        if (status != null && !status.trim().isEmpty()) qw.eq("status", status.trim());
        if (searchText != null && !searchText.trim().isEmpty()) {
            qw.and(w -> w.like("student_name", searchText.trim())
                    .or()
                    .inSql("student_id", "SELECT student_id FROM students WHERE student_name LIKE '%" + searchText.trim() + "%' OR phone LIKE '%" + searchText.trim() + "%'"));
        }
        qw.orderByDesc("update_time");

        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<TrainingProgress> list = trainingProgressService.selectTrainingProgressList(qw);
        PageInfo<TrainingProgress> pageInfo = new PageInfo<>(list);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "进度统计（平均完成率等）")
    @GetMapping("/statistics")
    public AjaxResult statistics(@RequestParam(required = false) Long trainingId) {
        try {
            QueryWrapper<TrainingProgress> qw = new QueryWrapper<>();
            if (trainingId != null) {
                qw.eq("training_id", trainingId);
            }
            List<TrainingProgress> all = trainingProgressService.selectTrainingProgressList(qw);
            
            int totalCount = all != null ? all.size() : 0;
            if (totalCount == 0) {
                return AjaxResult.success(new StatisticsResult(0, 0, 0, 0, 0));
            }
            
            int totalPercent = 0;
            int completedCount = 0;
            int learningCount = 0;
            for (TrainingProgress p : all) {
                if (p.getProgressPercent() != null) {
                    totalPercent += p.getProgressPercent();
                }
                if ("已完成".equals(p.getStatus())) {
                    completedCount++;
                } else if ("学习中".equals(p.getStatus())) {
                    learningCount++;
                }
            }
            
            int avgPercent = totalCount > 0 ? totalPercent / totalCount : 0;
            int completedRate = totalCount > 0 ? (int) Math.round(completedCount * 100.0 / totalCount) : 0;
            int needFollowCount = learningCount; // 待跟进学员：学习中且进度较低
            
            return AjaxResult.success(new StatisticsResult(avgPercent, completedRate, completedCount, learningCount, needFollowCount));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "进度详情（包含培训名称、学员信息）")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        try {
            TrainingProgress progress = trainingProgressService.getById(id);
            if (progress == null) {
                return AjaxResult.error("进度记录不存在");
            }
            
            ProgressDetail detail = new ProgressDetail();
            detail.setProgress(progress);
            
            // 获取培训信息
            if (progress.getTrainingId() != null) {
                Training training = trainingService.selectTrainingById(progress.getTrainingId());
                detail.setTraining(training);
            }
            
            // 获取学员信息
            if (progress.getStudentId() != null) {
                QueryWrapper<Students> sqw = new QueryWrapper<>();
                sqw.eq("student_id", progress.getStudentId());
                List<Students> students = studentsService.selectStudentsList(sqw);
                if (students != null && !students.isEmpty()) {
                    detail.setStudent(students.get(0));
                } else {
                    // 如果找不到，尝试通过 id 查找
                    Students student = studentsService.getById(progress.getStudentId());
                    detail.setStudent(student);
                }
            }
            
            // 获取资料进度明细
            if (progress.getTrainingId() != null && progress.getStudentId() != null) {
                List<TrainingMaterialProgress> materialProgressList = trainingMaterialProgressService.selectList(
                        new QueryWrapper<TrainingMaterialProgress>()
                                .eq("training_id", progress.getTrainingId())
                                .eq("student_id", progress.getStudentId())
                                .orderByAsc("id"));
                detail.setMaterialProgressList(materialProgressList);
            }
            
            return AjaxResult.success(detail);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    // 内部类：统计结果
    public static class StatisticsResult {
        private int avgProgressPercent; // 平均完成率
        private int completedRate; // 完成率（已完成人数/总人数）
        private int completedCount; // 已完成人数
        private int learningCount; // 学习中人数
        private int needFollowCount; // 待跟进人数

        public StatisticsResult() {}
        public StatisticsResult(int avgProgressPercent, int completedRate, int completedCount, int learningCount, int needFollowCount) {
            this.avgProgressPercent = avgProgressPercent;
            this.completedRate = completedRate;
            this.completedCount = completedCount;
            this.learningCount = learningCount;
            this.needFollowCount = needFollowCount;
        }

        public int getAvgProgressPercent() { return avgProgressPercent; }
        public void setAvgProgressPercent(int avgProgressPercent) { this.avgProgressPercent = avgProgressPercent; }
        public int getCompletedRate() { return completedRate; }
        public void setCompletedRate(int completedRate) { this.completedRate = completedRate; }
        public int getCompletedCount() { return completedCount; }
        public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
        public int getLearningCount() { return learningCount; }
        public void setLearningCount(int learningCount) { this.learningCount = learningCount; }
        public int getNeedFollowCount() { return needFollowCount; }
        public void setNeedFollowCount(int needFollowCount) { this.needFollowCount = needFollowCount; }
    }

    // 内部类：进度详情
    public static class ProgressDetail {
        private TrainingProgress progress;
        private Training training;
        private Students student;
        private List<TrainingMaterialProgress> materialProgressList;

        public TrainingProgress getProgress() { return progress; }
        public void setProgress(TrainingProgress progress) { this.progress = progress; }
        public Training getTraining() { return training; }
        public void setTraining(Training training) { this.training = training; }
        public Students getStudent() { return student; }
        public void setStudent(Students student) { this.student = student; }
        public List<TrainingMaterialProgress> getMaterialProgressList() { return materialProgressList; }
        public void setMaterialProgressList(List<TrainingMaterialProgress> materialProgressList) { this.materialProgressList = materialProgressList; }
    }
}


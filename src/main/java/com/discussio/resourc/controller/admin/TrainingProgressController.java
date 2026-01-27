package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.TrainingMaterial;
import com.discussio.resourc.model.auto.TrainingMaterialProgress;
import com.discussio.resourc.model.auto.TrainingProgress;
import com.discussio.resourc.service.ITrainingMaterialProgressService;
import com.discussio.resourc.service.ITrainingMaterialService;
import com.discussio.resourc.service.ITrainingProgressService;
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

    @ApiOperation(value = "开始培训（初始化进度）")
    @PostMapping("/start")
    public AjaxResult start(@RequestParam Long trainingId,
                            @RequestParam Long studentId,
                            @RequestParam(required = false) String studentName) {
        try {
            List<TrainingMaterial> materials = trainingMaterialService.listByTrainingId(trainingId);
            int total = materials != null ? materials.size() : 0;

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

            // 计算总进度：完成数量/总数
            List<TrainingMaterial> materials = trainingMaterialService.listByTrainingId(body.getTrainingId());
            int total = materials != null ? materials.size() : 0;
            int completed = 0;
            if (total > 0) {
                List<TrainingMaterialProgress> mpList = trainingMaterialProgressService.selectList(
                        new QueryWrapper<TrainingMaterialProgress>()
                                .eq("training_id", body.getTrainingId())
                                .eq("student_id", body.getStudentId()));
                if (mpList != null) {
                    for (TrainingMaterialProgress mp : mpList) {
                        if (mp != null && mp.getCompleted() != null && mp.getCompleted() == 1) completed++;
                    }
                }
            }
            int percent = total <= 0 ? 0 : (int) Math.floor(completed * 100.0 / total);

            TrainingProgress p = new TrainingProgress();
            p.setTrainingId(body.getTrainingId());
            p.setStudentId(body.getStudentId());
            p.setTotalCount(total);
            p.setCompletedCount(completed);
            p.setProgressPercent(percent);
            p.setStatus((total > 0 && completed >= total) ? "已完成" : "学习中");
            p.setLastStudyTime(new Date());
            trainingProgressService.upsert(p);

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

    @ApiOperation(value = "进度列表（管理查看）")
    @GetMapping("/list")
    public ResultTable list(@RequestParam(required = false) Integer page,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Long trainingId,
                            @RequestParam(required = false) Long studentId,
                            @RequestParam(required = false) String status) {
        QueryWrapper<TrainingProgress> qw = new QueryWrapper<>();
        if (trainingId != null) qw.eq("training_id", trainingId);
        if (studentId != null) qw.eq("student_id", studentId);
        if (status != null && !status.trim().isEmpty()) qw.eq("status", status.trim());
        qw.orderByDesc("update_time");

        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<TrainingProgress> list = trainingProgressService.selectTrainingProgressList(qw);
        PageInfo<TrainingProgress> pageInfo = new PageInfo<>(list);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }
}


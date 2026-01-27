package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.Exam;
import com.discussio.resourc.service.IExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    @ApiOperation(value = "考试列表", notes = "获取考试列表")
    @GetMapping("/list")
    public ResultTable examList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String examDate) {
        QueryWrapper<Exam> queryWrapper = new QueryWrapper<>();
        
        // 考试名称搜索
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("exam_name", searchText.trim())
                .or()
                .like("exam_type", searchText.trim())
            );
        }
        
        // 考试类型筛选
        if (examType != null && !examType.trim().isEmpty()) {
            queryWrapper.eq("exam_type", examType.trim());
        }
        
        // 状态筛选
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq("status", status.trim());
        }
        
        // 考试日期筛选
        if (examDate != null && !examDate.trim().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(examDate.trim());
                queryWrapper.eq("exam_date", date);
            } catch (Exception e) {
                // 日期格式错误，忽略该筛选条件
            }
        }
        
        // 排序：按考试日期和开始时间倒序
        queryWrapper.orderByDesc("exam_date", "start_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<Exam> list = examService.selectExamList(queryWrapper);
        PageInfo<Exam> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "新增考试", notes = "新增考试")
    @PostMapping("/add")
    public AjaxResult examAdd(@RequestBody Exam exam) {
        try {
            return toAjax(examService.insertExam(exam));
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
    public AjaxResult examDetail(@PathVariable("id") Long id) {
        try {
            return AjaxResult.success(examService.selectExamById(id));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "修改考试", notes = "修改考试")
    @PostMapping("/edit")
    public AjaxResult examEdit(@RequestBody Exam exam) {
        try {
            return toAjax(examService.updateExam(exam));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}

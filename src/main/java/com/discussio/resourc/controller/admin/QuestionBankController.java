package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.QuestionBank;
import com.discussio.resourc.service.IQuestionBankService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线题库 Controller
 */
@Api(value = "在线题库管理")
@RestController
@RequestMapping("/QuestionBankController")
@CrossOrigin(origins = "*")
public class QuestionBankController extends BaseController {
    
    @Autowired
    private IQuestionBankService questionBankService;

    @ApiOperation(value = "题库列表", notes = "获取题库列表")
    @GetMapping("/list")
    public ResultTable questionBankList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        
        // 题目内容搜索
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("question_content", searchText.trim())
                .or()
                .like("category", searchText.trim())
            );
        }
        
        // 题目类型筛选
        if (questionType != null && !questionType.trim().isEmpty()) {
            queryWrapper.eq("question_type", questionType.trim());
        }
        
        // 分类筛选
        if (category != null && !category.trim().isEmpty()) {
            queryWrapper.eq("category", category.trim());
        }
        
        // 难度筛选
        if (difficulty != null && !difficulty.trim().isEmpty()) {
            queryWrapper.eq("difficulty", difficulty.trim());
        }
        
        // 排序：按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<QuestionBank> list = questionBankService.selectQuestionBankList(queryWrapper);
        PageInfo<QuestionBank> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "新增题目", notes = "新增题目")
    @PostMapping("/add")
    public AjaxResult questionBankAdd(@RequestBody QuestionBank questionBank) {
        try {
            return toAjax(questionBankService.insertQuestionBank(questionBank));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "删除题目", notes = "删除题目")
    @DeleteMapping("/remove")
    public AjaxResult questionBankRemove(@RequestParam String ids) {
        try {
            return toAjax(questionBankService.deleteQuestionBankByIds(ids));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "题目详情", notes = "获取题目详情")
    @GetMapping("/detail/{id}")
    public AjaxResult questionBankDetail(@PathVariable("id") Long id) {
        try {
            return AjaxResult.success(questionBankService.selectQuestionBankById(id));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @ApiOperation(value = "修改题目", notes = "修改题目")
    @PostMapping("/edit")
    public AjaxResult questionBankEdit(@RequestBody QuestionBank questionBank) {
        try {
            return toAjax(questionBankService.updateQuestionBank(questionBank));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}

package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.FeedbackModule;
import com.discussio.resourc.service.IFeedbackModuleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 反馈评价模块 Controller
 */
@Api(value = "反馈评价模块")
@RestController
@RequestMapping("/FeedbackModuleController")
@CrossOrigin(origins = "*")
public class FeedbackModuleController extends BaseController {
    
    @Autowired
    private IFeedbackModuleService feedbackModuleService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.discussio.resourc.common.support.PermissionHelper permissionHelper;

    @ApiOperation(value = "反馈评价模块列表", notes = "需 feedback:view 权限")
    @GetMapping("/list")
    public ResultTable FeedbackModulelist(Tablepar tablepar, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "feedback:view")) {
            return new com.discussio.resourc.common.domain.ResultTable(403, "无权限查看反馈", 0, java.util.Collections.emptyList());
        }
        QueryWrapper<FeedbackModule> queryWrapper = new QueryWrapper<>();
        if (tablepar != null && tablepar.getSearchText() != null && !tablepar.getSearchText().isEmpty()) {
            queryWrapper.like("module_name", tablepar.getSearchText());
        }
        
        PageHelper.startPage(tablepar != null && tablepar.getPage() != null ? tablepar.getPage() : 1, 
                           tablepar != null && tablepar.getLimit() != null ? tablepar.getLimit() : 10);
        List<FeedbackModule> list = feedbackModuleService.selectFeedbackModuleList(queryWrapper);
        PageInfo<FeedbackModule> page = new PageInfo<>(list);
        
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "根据模块类型获取列表", notes = "需 feedback:view 权限")
    @GetMapping("/listByType")
    public AjaxResult listByType(@RequestParam String moduleType, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "feedback:view")) {
            return AjaxResult.error(403, "无权限查看反馈");
        }
        try {
            return AjaxResult.success(feedbackModuleService.getFeedbackModuleListByModuleType(moduleType));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "反馈评价模块新增", notes = "反馈评价模块新增")
    @PostMapping("/add")
    public AjaxResult FeedbackModuleadd(@RequestBody FeedbackModule feedbackModule) {
        return toAjax(feedbackModuleService.insertFeedbackModule(feedbackModule));
    }

    @ApiOperation(value = "反馈评价模块删除", notes = "反馈评价模块删除")
    @DeleteMapping("/remove")
    public AjaxResult FeedbackModuleremove(@RequestParam String ids) {
        return toAjax(feedbackModuleService.deleteFeedbackModuleByIds(ids));
    }

    @ApiOperation(value = "反馈评价模块详情", notes = "需 feedback:view 权限")
    @GetMapping("/detail/{id}")
    public AjaxResult FeedbackModuledetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "feedback:view")) {
            return AjaxResult.error(403, "无权限查看反馈");
        }
        return AjaxResult.success(feedbackModuleService.selectFeedbackModuleById(id));
    }

    @ApiOperation(value = "反馈评价模块修改保存", notes = "反馈评价模块修改保存")
    @PostMapping("/edit")
    public AjaxResult FeedbackModuleeditSave(@RequestBody FeedbackModule feedbackModule) {
        return toAjax(feedbackModuleService.updateFeedbackModule(feedbackModule));
    }
}

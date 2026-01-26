package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.SystemSettings;
import com.discussio.resourc.service.ISystemSettingsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统设置选项 Controller
 */
@Api(value = "系统设置选项")
@RestController
@RequestMapping("/SystemSettingsController")
@CrossOrigin(origins = "*")
public class SystemSettingsController extends BaseController {
    
    private String prefix = "admin/systemSettings";
    
    @Autowired
    private ISystemSettingsService systemSettingsService;


    @ApiOperation(value = "系统设置选项列表", notes = "系统设置选项列表")
    @GetMapping("/list")
    public ResultTable SystemSettingslist(Tablepar tablepar) {
        QueryWrapper<SystemSettings> queryWrapper = new QueryWrapper<>();
        if (tablepar != null && tablepar.getSearchText() != null && !tablepar.getSearchText().isEmpty()) {
            queryWrapper.like("course_type", tablepar.getSearchText());
        }
        
        PageHelper.startPage(tablepar != null && tablepar.getPage() != null ? tablepar.getPage() : 1, 
                           tablepar != null && tablepar.getLimit() != null ? tablepar.getLimit() : 10);
        List<SystemSettings> list = systemSettingsService.list(queryWrapper);
        PageInfo<SystemSettings> page = new PageInfo<>(list);
        
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "系统设置选项新增", notes = "系统设置选项新增")
    @PostMapping("/add")
    public AjaxResult SystemSettingsadd(@RequestBody SystemSettings systemSettings) {
        return toAjax(systemSettingsService.insertSystemSettings(systemSettings));
    }

    @ApiOperation(value = "系统设置选项删除", notes = "系统设置选项删除")
    @DeleteMapping("/remove")
    public AjaxResult SystemSettingsremove(@RequestParam String ids) {
        return toAjax(systemSettingsService.deleteSystemSettingsByIds(ids));
    }

    @ApiOperation(value = "系统设置选项详情", notes = "获取系统设置选项详情")
    @GetMapping("/detail/{id}")
    public AjaxResult SystemSettingsdetail(@PathVariable("id") Long id) {
        return AjaxResult.success(systemSettingsService.selectSystemSettingsById(id));
    }

    @ApiOperation(value = "editSave", notes = "系统设置选项修改保存")
    @PostMapping("/edit")
    public AjaxResult SystemSettingseditSave(@RequestBody SystemSettings systemSettings) {
        return toAjax(systemSettingsService.updateSystemSettings(systemSettings));
    }
}

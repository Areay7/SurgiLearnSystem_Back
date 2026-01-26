package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.MobileAccess;
import com.discussio.resourc.service.IMobileAccessService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动访问支持 Controller
 */
@Api(value = "移动访问支持")
@RestController
@RequestMapping("/MobileAccessController")
@CrossOrigin(origins = "*")
public class MobileAccessController extends BaseController {
    
    @Autowired
    private IMobileAccessService mobileAccessService;

    @ApiOperation(value = "移动访问支持列表", notes = "移动访问支持列表")
    @GetMapping("/list")
    public ResultTable MobileAccesslist(Tablepar tablepar) {
        QueryWrapper<MobileAccess> queryWrapper = new QueryWrapper<>();
        if (tablepar != null && tablepar.getSearchText() != null && !tablepar.getSearchText().isEmpty()) {
            queryWrapper.like("user_id", tablepar.getSearchText());
        }
        
        PageHelper.startPage(tablepar != null && tablepar.getPage() != null ? tablepar.getPage() : 1, 
                           tablepar != null && tablepar.getLimit() != null ? tablepar.getLimit() : 10);
        List<MobileAccess> list = mobileAccessService.selectMobileAccessList(queryWrapper);
        PageInfo<MobileAccess> page = new PageInfo<>(list);
        
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "根据设备类型获取列表", notes = "根据设备类型获取移动访问列表")
    @GetMapping("/listByDeviceType")
    public AjaxResult listByDeviceType(@RequestParam String deviceType) {
        try {
            return AjaxResult.success(mobileAccessService.getMobileAccessListByDeviceType(deviceType));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "根据访问状态获取列表", notes = "根据访问状态获取移动访问列表")
    @GetMapping("/listByStatus")
    public AjaxResult listByStatus(@RequestParam String accessStatus) {
        try {
            return AjaxResult.success(mobileAccessService.getMobileAccessListByAccessStatus(accessStatus));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "移动访问支持新增", notes = "移动访问支持新增")
    @PostMapping("/add")
    public AjaxResult MobileAccessadd(@RequestBody MobileAccess mobileAccess) {
        return toAjax(mobileAccessService.insertMobileAccess(mobileAccess));
    }

    @ApiOperation(value = "移动访问支持删除", notes = "移动访问支持删除")
    @DeleteMapping("/remove")
    public AjaxResult MobileAccessremove(@RequestParam String ids) {
        return toAjax(mobileAccessService.deleteMobileAccessByIds(ids));
    }

    @ApiOperation(value = "移动访问支持详情", notes = "获取移动访问支持详情")
    @GetMapping("/detail/{id}")
    public AjaxResult MobileAccessdetail(@PathVariable("id") Long id) {
        return AjaxResult.success(mobileAccessService.selectMobileAccessById(id));
    }

    @ApiOperation(value = "移动访问支持修改保存", notes = "移动访问支持修改保存")
    @PostMapping("/edit")
    public AjaxResult MobileAccesseditSave(@RequestBody MobileAccess mobileAccess) {
        return toAjax(mobileAccessService.updateMobileAccess(mobileAccess));
    }
}

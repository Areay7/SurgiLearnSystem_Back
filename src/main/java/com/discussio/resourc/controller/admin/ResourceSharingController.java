package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.ResourceSharing;
import com.discussio.resourc.service.IResourceSharingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源共享平台 Controller
 */
@Api(value = "资源共享平台")
@RestController
@RequestMapping("/ResourceSharingController")
@CrossOrigin(origins = "*")
public class ResourceSharingController extends BaseController {
    
    private String prefix = "admin/resourceSharing";
    
    @Autowired
    private IResourceSharingService resourceSharingService;

    @ApiOperation(value = "根据资源类型获取资源列表", notes = "根据资源类型获取资源列表")
    @GetMapping("/listByType")
    public AjaxResult listByType(@RequestParam String resourceType) {
        try {
            return AjaxResult.success(resourceSharingService.getResourceSharingListByResourceType(resourceType));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "资源共享平台列表", notes = "资源共享平台列表")
    @GetMapping("/list")
    public ResultTable ResourceSharinglist(Tablepar tablepar) {
        QueryWrapper<ResourceSharing> queryWrapper = new QueryWrapper<>();
        if (tablepar != null && tablepar.getSearchText() != null && !tablepar.getSearchText().isEmpty()) {
            queryWrapper.like("resource_name", tablepar.getSearchText());
        }
        
        PageHelper.startPage(tablepar != null && tablepar.getPage() != null ? tablepar.getPage() : 1, 
                           tablepar != null && tablepar.getLimit() != null ? tablepar.getLimit() : 10);
        List<ResourceSharing> list = resourceSharingService.selectResourceSharingList(queryWrapper);
        PageInfo<ResourceSharing> page = new PageInfo<>(list);
        
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "资源共享平台新增", notes = "资源共享平台新增")
    @PostMapping("/add")
    public AjaxResult ResourceSharingadd(@RequestBody ResourceSharing resourceSharing) {
        return toAjax(resourceSharingService.insertResourceSharing(resourceSharing));
    }

    @ApiOperation(value = "资源共享平台删除", notes = "资源共享平台删除")
    @DeleteMapping("/remove")
    public AjaxResult ResourceSharingremove(@RequestParam String ids) {
        return toAjax(resourceSharingService.deleteResourceSharingByIds(ids));
    }

    @ApiOperation(value = "资源共享平台详情", notes = "获取资源共享平台详情")
    @GetMapping("/detail/{id}")
    public AjaxResult ResourceSharingdetail(@PathVariable("id") Long id) {
        return AjaxResult.success(resourceSharingService.selectResourceSharingById(id));
    }

    @ApiOperation(value = "editSave", notes = "资源共享平台修改保存")
    @PostMapping("/edit")
    public AjaxResult ResourceSharingeditSave(@RequestBody ResourceSharing resourceSharing) {
        return toAjax(resourceSharingService.updateResourceSharing(resourceSharing));
    }
}

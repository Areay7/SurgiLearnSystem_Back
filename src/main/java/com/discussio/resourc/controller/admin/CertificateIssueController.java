package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.CertificateIssue;
import com.discussio.resourc.service.ICertificateIssueService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 证书颁发功能 Controller
 */
@Api(value = "证书颁发功能")
@RestController
@RequestMapping("/CertificateIssueController")
@CrossOrigin(origins = "*")
public class CertificateIssueController extends BaseController {
    
    @Autowired
    private ICertificateIssueService certificateIssueService;

    @ApiOperation(value = "证书颁发功能列表", notes = "证书颁发功能列表")
    @GetMapping("/list")
    public ResultTable CertificateIssuelist(Tablepar tablepar) {
        QueryWrapper<CertificateIssue> queryWrapper = new QueryWrapper<>();
        if (tablepar != null && tablepar.getSearchText() != null && !tablepar.getSearchText().isEmpty()) {
            queryWrapper.like("holder_name", tablepar.getSearchText());
        }
        
        PageHelper.startPage(tablepar != null && tablepar.getPage() != null ? tablepar.getPage() : 1, 
                           tablepar != null && tablepar.getLimit() != null ? tablepar.getLimit() : 10);
        List<CertificateIssue> list = certificateIssueService.selectCertificateIssueList(queryWrapper);
        PageInfo<CertificateIssue> page = new PageInfo<>(list);
        
        return pageTable(page.getList(), page.getTotal());
    }

    @ApiOperation(value = "根据证书类型获取列表", notes = "根据证书类型获取证书列表")
    @GetMapping("/listByType")
    public AjaxResult listByType(@RequestParam String certificateType) {
        try {
            return AjaxResult.success(certificateIssueService.getCertificateIssueListByCertificateType(certificateType));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "根据证书状态获取列表", notes = "根据证书状态获取证书列表")
    @GetMapping("/listByStatus")
    public AjaxResult listByStatus(@RequestParam String certificateStatus) {
        try {
            return AjaxResult.success(certificateIssueService.getCertificateIssueListByCertificateStatus(certificateStatus));
        } catch (Exception e) {
            return AjaxResult.error("查询失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "证书颁发功能新增", notes = "证书颁发功能新增")
    @PostMapping("/add")
    public AjaxResult CertificateIssueadd(@RequestBody CertificateIssue certificateIssue) {
        return toAjax(certificateIssueService.insertCertificateIssue(certificateIssue));
    }

    @ApiOperation(value = "证书颁发功能删除", notes = "证书颁发功能删除")
    @DeleteMapping("/remove")
    public AjaxResult CertificateIssueremove(@RequestParam String ids) {
        return toAjax(certificateIssueService.deleteCertificateIssueByIds(ids));
    }

    @ApiOperation(value = "证书颁发功能详情", notes = "获取证书颁发功能详情")
    @GetMapping("/detail/{id}")
    public AjaxResult CertificateIssuedetail(@PathVariable("id") Long id) {
        return AjaxResult.success(certificateIssueService.selectCertificateIssueById(id));
    }

    @ApiOperation(value = "证书颁发功能修改保存", notes = "证书颁发功能修改保存")
    @PostMapping("/edit")
    public AjaxResult CertificateIssueeditSave(@RequestBody CertificateIssue certificateIssue) {
        return toAjax(certificateIssueService.updateCertificateIssue(certificateIssue));
    }
}

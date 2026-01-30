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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

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

    @Value("${file.upload-path:./uploads}")
    private String uploadRoot;

    private static final String[] STAMP_ALLOWED_EXT = new String[]{".png", ".jpg", ".jpeg", ".webp"};

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

    @ApiOperation(value = "我的证书列表", notes = "按 holder_id 查询当前用户证书（学员/讲师端使用）")
    @GetMapping("/myList")
    public ResultTable myList(@RequestParam String holderId,
                              @RequestParam(required = false) Integer page,
                              @RequestParam(required = false) Integer limit) {
        QueryWrapper<CertificateIssue> qw = new QueryWrapper<>();
        qw.eq("holder_id", holderId);
        qw.orderByDesc("issue_date").orderByDesc("id");
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<CertificateIssue> list = certificateIssueService.selectCertificateIssueList(qw);
        PageInfo<CertificateIssue> p = new PageInfo<>(list);
        return pageTable(p.getList(), p.getTotal());
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

    @ApiOperation(value = "上传证书盖章图片", notes = "上传盖章图片（png/jpg/webp），返回 stampKey 供预览/保存")
    @PostMapping(value = "/uploadStamp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadStamp(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) return AjaxResult.error("文件不能为空");
            String name = file.getOriginalFilename();
            if (name == null) return AjaxResult.error("文件名不能为空");
            String lower = name.toLowerCase();
            boolean ok = false;
            for (String ext : STAMP_ALLOWED_EXT) {
                if (lower.endsWith(ext)) { ok = true; break; }
            }
            if (!ok) return AjaxResult.error("仅支持 png/jpg/jpeg/webp");

            File dir = Paths.get(uploadRoot, "certificates", "stamps").toFile();
            if (!dir.exists()) dir.mkdirs();
            String ext = lower.substring(lower.lastIndexOf('.'));
            String key = UUID.randomUUID().toString().replace("-", "") + ext;
            Path target = Paths.get(dir.getAbsolutePath(), key);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // 返回 key，前端通过 /stamp/{key} 预览
            return AjaxResult.success("上传成功", key);
        } catch (Exception e) {
            return AjaxResult.error("上传失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "预览盖章图片", notes = "根据 stampKey 获取盖章图片")
    @GetMapping("/stamp/{key}")
    public ResponseEntity<Resource> previewStamp(@PathVariable("key") String key) {
        try {
            if (key == null || key.trim().isEmpty()) return ResponseEntity.notFound().build();
            File file = Paths.get(uploadRoot, "certificates", "stamps", key).toFile();
            if (!file.exists()) return ResponseEntity.notFound().build();
            Resource res = new FileSystemResource(file);
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) contentType = "image/png";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(res);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

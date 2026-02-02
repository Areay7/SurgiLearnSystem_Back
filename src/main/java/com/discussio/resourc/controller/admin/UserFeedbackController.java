package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.common.support.PermissionHelper;
import com.discussio.resourc.model.auto.UserFeedback;
import com.discussio.resourc.service.IUserFeedbackService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 用户反馈评价 Controller
 */
@Api(value = "用户反馈评价")
@RestController
@RequestMapping("/UserFeedbackController")
@CrossOrigin(origins = "*")
public class UserFeedbackController extends BaseController {

    @Autowired
    private IUserFeedbackService userFeedbackService;
    @Autowired(required = false)
    private PermissionHelper permissionHelper;

    private boolean hasViewPermission(HttpServletRequest request) {
        return permissionHelper == null || permissionHelper.hasPermission(request, "feedback:view");
    }

    private boolean hasSubmitPermission(HttpServletRequest request) {
        return permissionHelper == null || permissionHelper.hasPermission(request, "feedback:submit");
    }

    private boolean hasManagePermission(HttpServletRequest request) {
        return permissionHelper == null || permissionHelper.hasPermission(request, "feedback:manage");
    }

    @ApiOperation(value = "反馈列表", notes = "需 feedback:view；管理员看全部，学员看自己的")
    @GetMapping("/list")
    public ResultTable list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String feedbackType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        if (!hasViewPermission(request)) {
            return new ResultTable(403, "无权限", 0, java.util.Collections.emptyList());
        }
        QueryWrapper<UserFeedback> qw = new QueryWrapper<>();
        String phone = permissionHelper != null ? permissionHelper.parseUserPhone(request) : null;
        boolean canViewAll = permissionHelper != null && permissionHelper.hasPermission(request, "user:view");
        if (phone != null && !phone.trim().isEmpty() && !canViewAll) {
            qw.eq("user_id", phone.trim());
        }
        if (StringUtils.isNotBlank(feedbackType)) qw.eq("feedback_type", feedbackType.trim());
        if (StringUtils.isNotBlank(status)) qw.eq("status", status.trim());
        if (StringUtils.isNotBlank(keyword)) {
            qw.and(w -> w.like("title", keyword).or().like("content", keyword).or().like("user_name", keyword));
        }
        qw.orderByDesc("create_time");

        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<UserFeedback> list = userFeedbackService.list(qw);
        PageInfo<UserFeedback> pageInfo = new PageInfo<>(list);
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "提交反馈", notes = "需 feedback:submit")
    @PostMapping("/add")
    public AjaxResult add(@RequestBody UserFeedback feedback, HttpServletRequest request) {
        if (!hasSubmitPermission(request)) return AjaxResult.error(403, "无权限提交反馈");
        String phone = permissionHelper != null ? permissionHelper.parseUserPhone(request) : null;
        if (phone == null || phone.trim().isEmpty()) return AjaxResult.error("请先登录");
        feedback.setUserId(phone.trim());
        if (StringUtils.isBlank(feedback.getUserName())) {
            feedback.setUserName(phone);
        }
        if (StringUtils.isBlank(feedback.getTitle())) return AjaxResult.error("标题不能为空");
        if (StringUtils.isBlank(feedback.getContent())) return AjaxResult.error("内容不能为空");
        if (feedback.getFeedbackType() == null || feedback.getFeedbackType().trim().isEmpty()) {
            feedback.setFeedbackType("系统建议");
        }
        if (feedback.getRating() == null) feedback.setRating(0);
        feedback.setRating(Math.max(0, Math.min(5, feedback.getRating())));
        return toAjax(userFeedbackService.save(feedback) ? 1 : 0);
    }

    @ApiOperation(value = "反馈详情")
    @GetMapping("/detail/{id}")
    public AjaxResult detail(@PathVariable Long id, HttpServletRequest request) {
        if (!hasViewPermission(request)) return AjaxResult.error(403, "无权限");
        UserFeedback f = userFeedbackService.getById(id);
        if (f == null) return AjaxResult.error("记录不存在");
        return AjaxResult.success(f);
    }

    @ApiOperation(value = "更新状态/回复", notes = "需 feedback:view")
    @PostMapping("/update")
    public AjaxResult update(@RequestBody UserFeedback feedback, HttpServletRequest request) {
        if (!hasManagePermission(request)) return AjaxResult.error(403, "无权限");
        if (feedback.getId() == null) return AjaxResult.error("ID不能为空");
        UserFeedback existing = userFeedbackService.getById(feedback.getId());
        if (existing == null) return AjaxResult.error("记录不存在");
        if (feedback.getStatus() != null) existing.setStatus(feedback.getStatus());
        if (feedback.getReplyContent() != null) {
            existing.setReplyContent(feedback.getReplyContent());
            existing.setReplyTime(new Date());
        }
        return toAjax(userFeedbackService.updateById(existing) ? 1 : 0);
    }

    @ApiOperation(value = "删除反馈", notes = "需 feedback:view")
    @DeleteMapping("/remove/{id}")
    public AjaxResult remove(@PathVariable Long id, HttpServletRequest request) {
        if (!hasManagePermission(request)) return AjaxResult.error(403, "无权限");
        return toAjax(userFeedbackService.removeById(id) ? 1 : 0);
    }
}

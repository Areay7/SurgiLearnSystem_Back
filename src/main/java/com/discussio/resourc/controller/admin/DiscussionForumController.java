package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.custom.Tablepar;
import com.discussio.resourc.model.auto.DiscussionForum;
import com.discussio.resourc.service.IDiscussionForumService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 讨论论坛模块 Controller
 */
@Api(value = "讨论论坛模块")
@RestController
@RequestMapping("/DiscussionForumController")
@CrossOrigin(origins = "*")
public class DiscussionForumController extends BaseController {
    
    private String prefix = "admin/discussionForum";
    
    @Autowired
    private IDiscussionForumService discussionForumService;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.discussio.resourc.common.support.PermissionHelper permissionHelper;

    @ApiOperation(value = "讨论论坛模块列表", notes = "需 forum:view 权限")
    @GetMapping("/list")
    public ResultTable DiscussionForumlist(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String forumTitle,
            @RequestParam(required = false) String posterId,
            @RequestParam(required = false) String postTime,
            HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "forum:view")) {
            return new com.discussio.resourc.common.domain.ResultTable(403, "无权限查看论坛", 0, java.util.Collections.emptyList());
        }
        QueryWrapper<DiscussionForum> queryWrapper = new QueryWrapper<>();
        
        // 讨论标题搜索
        if (forumTitle != null && !forumTitle.trim().isEmpty()) {
            queryWrapper.like("forum_title", forumTitle.trim());
        }
        
        // 发帖者ID搜索
        if (posterId != null && !posterId.trim().isEmpty()) {
            queryWrapper.like("poster_id", posterId.trim());
        }
        
        // 发布时间搜索（按日期匹配）
        if (postTime != null && !postTime.trim().isEmpty()) {
            // 前端传的是日期格式 YYYY-MM-DD，需要匹配当天的所有记录
            queryWrapper.apply("DATE(post_time) = {0}", postTime.trim());
        }
        
        // 通用搜索（兼容旧接口）
        if (searchText != null && !searchText.trim().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                .like("forum_title", searchText)
                .or()
                .like("content", searchText)
            );
        }
        
        // 分类筛选
        if (categoryId != null && !categoryId.trim().isEmpty()) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        // 排序：置顶优先，然后按发布时间倒序
        queryWrapper.orderByDesc("is_sticky", "post_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 10);
        List<DiscussionForum> list = discussionForumService.selectDiscussionForumList(queryWrapper);
        PageInfo<DiscussionForum> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }

    @ApiOperation(value = "获取分类统计", notes = "需 forum:view 权限")
    @GetMapping("/categoryStats")
    public AjaxResult getCategoryStats(HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "forum:view")) {
            return AjaxResult.error(403, "无权限查看论坛");
        }
        try {
            List<DiscussionForum> allList = discussionForumService.list();
            // 统计各分类数量
            java.util.Map<String, Integer> stats = new java.util.HashMap<>();
            stats.put("护理技巧", 0);
            stats.put("案例分析", 0);
            stats.put("经验分享", 0);
            stats.put("其他", 0);
            
            for (DiscussionForum forum : allList) {
                String categoryId = forum.getCategoryId();
                if ("C001".equals(categoryId)) {
                    stats.put("护理技巧", stats.get("护理技巧") + 1);
                } else if ("C002".equals(categoryId)) {
                    stats.put("案例分析", stats.get("案例分析") + 1);
                } else if ("C003".equals(categoryId)) {
                    stats.put("经验分享", stats.get("经验分享") + 1);
                } else {
                    stats.put("其他", stats.get("其他") + 1);
                }
            }
            return AjaxResult.success(stats);
        } catch (Exception e) {
            return AjaxResult.error("统计失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "讨论论坛模块新增", notes = "需 forum:post 权限")
    @PostMapping("/add")
    public AjaxResult DiscussionForumadd(@RequestBody DiscussionForum discussionForum, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "forum:post")) {
            return AjaxResult.error(403, "无权限发帖");
        }
        return toAjax(discussionForumService.insertDiscussionForum(discussionForum));
    }

    @ApiOperation(value = "讨论论坛模块删除", notes = "需 forum:manage 权限")
    @DeleteMapping("/remove")
    public AjaxResult DiscussionForumremove(@RequestParam String ids, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "forum:manage")) {
            return AjaxResult.error(403, "无权限删除帖子");
        }
        return toAjax(discussionForumService.deleteDiscussionForumByIds(ids));
    }

    @ApiOperation(value = "讨论论坛模块详情", notes = "需 forum:view 权限")
    @GetMapping("/detail/{id}")
    public AjaxResult DiscussionForumdetail(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "forum:view")) {
            return AjaxResult.error(403, "无权限查看帖子");
        }
        return AjaxResult.success(discussionForumService.selectDiscussionForumById(id));
    }

    @ApiOperation(value = "editSave", notes = "需 forum:post 或 forum:manage 权限")
    @PostMapping("/edit")
    public AjaxResult DiscussionForumeditSave(@RequestBody DiscussionForum discussionForum, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasAnyPermission(request, "forum:post", "forum:manage")) {
            return AjaxResult.error(403, "无权限编辑帖子");
        }
        return toAjax(discussionForumService.updateDiscussionForum(discussionForum));
    }
    
    @ApiOperation(value = "点赞话题", notes = "需 forum:post 权限")
    @PostMapping("/like/{id}")
    public AjaxResult likeTopic(@PathVariable("id") Long id, HttpServletRequest request) {
        if (permissionHelper != null && !permissionHelper.hasPermission(request, "forum:post")) {
            return AjaxResult.error(403, "无权限操作");
        }
        DiscussionForum forum = discussionForumService.selectDiscussionForumById(id);
        if (forum == null) {
            return AjaxResult.error("话题不存在");
        }
        forum.setLikeCount((forum.getLikeCount() == null ? 0 : forum.getLikeCount()) + 1);
        return toAjax(discussionForumService.updateDiscussionForum(forum));
    }
}

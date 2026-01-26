package com.discussio.resourc.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.discussio.resourc.common.config.BaseController;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.discussio.resourc.model.auto.ForumReply;
import com.discussio.resourc.model.auto.DiscussionForum;
import com.discussio.resourc.service.IDiscussionForumService;
import com.discussio.resourc.service.IForumReplyService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 讨论论坛回复模块 Controller
 */
@Api(value = "讨论论坛回复模块")
@RestController
@RequestMapping("/ForumReplyController")
@CrossOrigin(origins = "*")
public class ForumReplyController extends BaseController {
    
    @Autowired
    private IForumReplyService forumReplyService;
    
    @Autowired
    private IDiscussionForumService discussionForumService;
    
    @ApiOperation(value = "获取话题的回复列表", notes = "获取话题的回复列表")
    @GetMapping("/list")
    public ResultTable getReplyList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = true) Long forumId) {
        QueryWrapper<ForumReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("forum_id", forumId);
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.orderByAsc("reply_time");
        
        PageHelper.startPage(page != null ? page : 1, limit != null ? limit : 20);
        List<ForumReply> list = forumReplyService.list(queryWrapper);
        PageInfo<ForumReply> pageInfo = new PageInfo<>(list);
        
        return pageTable(pageInfo.getList(), pageInfo.getTotal());
    }
    
    @ApiOperation(value = "新增回复", notes = "新增回复")
    @PostMapping("/add")
    public AjaxResult addReply(@RequestBody ForumReply forumReply) {
        // 设置回复时间
        if (forumReply.getReplyTime() == null) {
            forumReply.setReplyTime(new Date());
        }
        int result = forumReplyService.insertForumReply(forumReply);
        
        // 更新话题的回复数量和最后回复信息
        if (result > 0) {
            updateForumReplyInfo(forumReply.getForumId(), forumReply.getReplierId());
        }
        
        return toAjax(result);
    }
    
    @ApiOperation(value = "删除回复", notes = "删除回复")
    @DeleteMapping("/remove")
    public AjaxResult removeReply(@RequestParam String ids) {
        return toAjax(forumReplyService.deleteForumReplyByIds(ids));
    }
    
    @ApiOperation(value = "获取回复详情", notes = "获取回复详情")
    @GetMapping("/detail/{id}")
    public AjaxResult getReplyDetail(@PathVariable("id") Long id) {
        return AjaxResult.success(forumReplyService.selectForumReplyById(id));
    }
    
    @ApiOperation(value = "更新回复", notes = "更新回复")
    @PostMapping("/edit")
    public AjaxResult updateReply(@RequestBody ForumReply forumReply) {
        return toAjax(forumReplyService.updateForumReply(forumReply));
    }
    
    @ApiOperation(value = "点赞回复", notes = "点赞回复")
    @PostMapping("/like/{id}")
    public AjaxResult likeReply(@PathVariable("id") Long id) {
        ForumReply reply = forumReplyService.selectForumReplyById(id);
        if (reply == null) {
            return AjaxResult.error("回复不存在");
        }
        reply.setLikeCount((reply.getLikeCount() == null ? 0 : reply.getLikeCount()) + 1);
        return toAjax(forumReplyService.updateForumReply(reply));
    }
    
    /**
     * 更新话题的回复数量和最后回复信息
     */
    private void updateForumReplyInfo(Long forumId, String replierId) {
        try {
            DiscussionForum forum = discussionForumService.getById(forumId);
            if (forum != null) {
                // 统计回复数量
                QueryWrapper<ForumReply> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("forum_id", forumId);
                queryWrapper.eq("is_deleted", 0);
                long replyCount = forumReplyService.count(queryWrapper);
                
                forum.setReplyCount((int) replyCount);
                forum.setLastReplyId(replierId);
                forum.setLastReplyTime(new Date());
                
                discussionForumService.updateById(forum);
            }
        } catch (Exception e) {
            // 记录日志但不影响回复的创建
            e.printStackTrace();
        }
    }
}

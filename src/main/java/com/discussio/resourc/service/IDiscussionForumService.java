package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.DiscussionForum;

import java.util.List;

/**
 * 讨论论坛模块Service接口
 */
public interface IDiscussionForumService extends IService<DiscussionForum> {
    DiscussionForum selectDiscussionForumById(Long id);
    List<DiscussionForum> selectDiscussionForumList(Wrapper<DiscussionForum> queryWrapper);
    List<DiscussionForum> selectDiscussionForumList(DiscussionForum discussionForum);
    int insertDiscussionForum(DiscussionForum discussionForum);
    int updateDiscussionForum(DiscussionForum discussionForum);
    int deleteDiscussionForumByIds(String ids);
    int deleteDiscussionForumById(Long id);
    int updateDiscussionForumVisible(DiscussionForum discussionForum);
}

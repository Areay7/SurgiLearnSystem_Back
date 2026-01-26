package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.ForumReply;

/**
 * 讨论论坛回复Service接口
 */
public interface IForumReplyService extends IService<ForumReply> {
    ForumReply selectForumReplyById(Long id);
    int insertForumReply(ForumReply forumReply);
    int updateForumReply(ForumReply forumReply);
    int deleteForumReplyByIds(String ids);
}

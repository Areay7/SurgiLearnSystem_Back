package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.ForumReplyMapper;
import com.discussio.resourc.model.auto.ForumReply;
import com.discussio.resourc.service.IForumReplyService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 讨论论坛回复Service业务处理
 */
@Service
public class ForumReplyServiceImpl extends ServiceImpl<ForumReplyMapper, ForumReply>
        implements IForumReplyService {
    
    @Override
    public ForumReply selectForumReplyById(Long id) {
        return baseMapper.selectById(id);
    }
    
    @Override
    public int insertForumReply(ForumReply forumReply) {
        if (forumReply.getReplyTime() == null) {
            forumReply.setReplyTime(new Date());
        }
        if (forumReply.getLikeCount() == null) {
            forumReply.setLikeCount(0);
        }
        if (forumReply.getIsDeleted() == null) {
            forumReply.setIsDeleted(0);
        }
        return baseMapper.insert(forumReply);
    }
    
    @Override
    public int updateForumReply(ForumReply forumReply) {
        return baseMapper.updateById(forumReply);
    }
    
    @Override
    public int deleteForumReplyByIds(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        int count = 0;
        for (String id : idList) {
            Long replyId = ConvertUtil.toLong(id);
            if (replyId != null) {
                count += baseMapper.deleteById(replyId);
            }
        }
        return count;
    }
}

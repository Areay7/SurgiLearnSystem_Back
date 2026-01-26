package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.ForumReply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讨论论坛回复Mapper接口
 */
@Mapper
public interface ForumReplyMapper extends BaseMapper<ForumReply> {
    ForumReply selectForumReplyById(Long id);
}

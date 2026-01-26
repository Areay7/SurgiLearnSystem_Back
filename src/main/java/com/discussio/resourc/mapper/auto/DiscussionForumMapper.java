package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.DiscussionForum;
import org.apache.ibatis.annotations.Mapper;

/**
 * 讨论论坛模块Mapper接口
 */
@Mapper
public interface DiscussionForumMapper extends BaseMapper<DiscussionForum> {
    DiscussionForum selectDiscussionForumById(Long id);
}

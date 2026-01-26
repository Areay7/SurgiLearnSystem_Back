package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录讨论论坛Mapper接口
 */
@Mapper
public interface LoginDiscussionForumMapper extends BaseMapper<LoginDiscussionForum> {
    LoginDiscussionForum findByPaymentschedulename(String username);
    LoginDiscussionForum findBydiscussionForumname(String username);
}

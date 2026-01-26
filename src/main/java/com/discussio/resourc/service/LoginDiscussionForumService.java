package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.LoginDiscussionForum;

/**
 * 登录服务接口
 */
public interface LoginDiscussionForumService extends IService<LoginDiscussionForum> {
    boolean loginDiscussionForum(String username, String password);
    boolean registerUser(String username, String password);
    boolean changePassword(String username, String oldPassword, String newPassword);
    String generateToken(String username);
}

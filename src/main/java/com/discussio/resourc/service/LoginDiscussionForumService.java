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
    /**
     * 管理员重置指定用户密码（无需旧密码）
     */
    boolean adminResetPassword(String username, String newPassword);
    String generateToken(String username);
    /**
     * 从 JWT token 中解析用户名（token 不含 Bearer 前缀）
     */
    String parseUsernameFromToken(String token);
    LoginDiscussionForum getUserInfo(String username);
    boolean updateUserInfo(String username, String nickname);
}

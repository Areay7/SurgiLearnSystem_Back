package com.discussio.resourc.controller;

import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.service.LoginDiscussionForumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器
 */
@Api(value = "登录接口")
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class LoginController {
    
    @Autowired
    private LoginDiscussionForumService loginService;

    @ApiOperation(value = "用户登录", notes = "使用手机号和密码登录")
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginRequest request) {
        try {
            // 参数验证
            if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
                return AjaxResult.error("手机号不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return AjaxResult.error("密码不能为空");
            }
            
            boolean success = loginService.loginDiscussionForum(request.getPhone().trim(), request.getPassword());
            if (success) {
                String token = loginService.generateToken(request.getPhone().trim());
                return AjaxResult.success("登录成功", token);
            } else {
                return AjaxResult.error("用户名或密码错误");
            }
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return AjaxResult.error("登录失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "用户注册", notes = "新用户注册")
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterRequest request) {
        try {
            // 参数验证
            if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
                return AjaxResult.error("手机号不能为空");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return AjaxResult.error("密码不能为空");
            }
            if (request.getPassword().length() < 6) {
                return AjaxResult.error("密码长度至少6位");
            }
            
            boolean success = loginService.registerUser(request.getPhone().trim(), request.getPassword());
            if (success) {
                return AjaxResult.success("注册成功，请登录");
            } else {
                return AjaxResult.error("用户名已存在");
            }
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return AjaxResult.error("注册失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "修改密码", notes = "修改用户密码")
    @PostMapping("/changePassword")
    public AjaxResult changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            boolean success = loginService.changePassword(
                request.getPhone(), 
                request.getOldPassword(), 
                request.getNewPassword()
            );
            if (success) {
                return AjaxResult.success("密码修改成功");
            } else {
                return AjaxResult.error("密码修改失败，请检查旧密码是否正确");
            }
        } catch (Exception e) {
            return AjaxResult.error("密码修改失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "获取用户信息", notes = "获取当前登录用户信息")
    @GetMapping("/userInfo")
    public AjaxResult getUserInfo(@RequestParam String username) {
        try {
            com.discussio.resourc.model.auto.LoginDiscussionForum user = loginService.getUserInfo(username);
            if (user != null) {
                // 不返回密码
                user.setPassword(null);
                return AjaxResult.success(user);
            } else {
                return AjaxResult.error("用户不存在");
            }
        } catch (Exception e) {
            return AjaxResult.error("获取用户信息失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "更新用户信息", notes = "更新用户昵称等信息")
    @PostMapping("/updateUserInfo")
    public AjaxResult updateUserInfo(@RequestBody UpdateUserInfoRequest request) {
        try {
            boolean success = loginService.updateUserInfo(request.getUsername(), request.getNickname());
            if (success) {
                return AjaxResult.success("更新成功");
            } else {
                return AjaxResult.error("更新失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("更新失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "批量获取用户信息", notes = "根据用户名列表批量获取用户信息（包括昵称）")
    @PostMapping("/batchUserInfo")
    public AjaxResult batchUserInfo(@RequestBody BatchUserInfoRequest request) {
        try {
            java.util.Map<String, String> userInfoMap = new java.util.HashMap<>();
            if (request.getUsernames() != null) {
                for (String username : request.getUsernames()) {
                    com.discussio.resourc.model.auto.LoginDiscussionForum user = loginService.getUserInfo(username);
                    if (user != null) {
                        // 优先返回昵称，如果没有昵称则返回用户名
                        String displayName = user.getNickname();
                        if (displayName == null || displayName.trim().isEmpty()) {
                            displayName = user.getUsername();
                        }
                        userInfoMap.put(username, displayName);
                    } else {
                        userInfoMap.put(username, username); // 用户不存在时返回原用户名
                    }
                }
            }
            return AjaxResult.success(userInfoMap);
        } catch (Exception e) {
            return AjaxResult.error("获取用户信息失败：" + e.getMessage());
        }
    }

    // 内部类：登录请求
    public static class LoginRequest {
        private String phone;
        private String password;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // 内部类：注册请求
    public static class RegisterRequest {
        private String phone;
        private String password;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // 内部类：修改密码请求
    public static class ChangePasswordRequest {
        private String phone;
        private String oldPassword;
        private String newPassword;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
    
    // 内部类：更新用户信息请求
    public static class UpdateUserInfoRequest {
        private String username;
        private String nickname;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
    
    // 内部类：批量获取用户信息请求
    public static class BatchUserInfoRequest {
        private java.util.List<String> usernames;

        public java.util.List<String> getUsernames() {
            return usernames;
        }

        public void setUsernames(java.util.List<String> usernames) {
            this.usernames = usernames;
        }
    }
}

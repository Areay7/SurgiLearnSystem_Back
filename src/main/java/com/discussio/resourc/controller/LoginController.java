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
}

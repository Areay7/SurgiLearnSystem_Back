package com.discussio.resourc.controller;

import com.discussio.resourc.common.config.MultiFormatDateDeserializer;
import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.LoginDiscussionForumService;
import com.discussio.resourc.service.IStudentsService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
    
    @Autowired
    private IStudentsService studentsService;

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
                String username = request.getPhone().trim();
                String token = loginService.generateToken(username);
                com.discussio.resourc.model.auto.LoginDiscussionForum user = loginService.getUserInfo(username);
                java.util.Map<String, Object> data = new java.util.HashMap<>();
                data.put("token", token);
                data.put("userType", user != null && user.getUserType() != null ? user.getUserType() : 0);
                return AjaxResult.success("登录成功", data);
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
                // 确保学员记录存在（注册用户默认为学员）
                if (studentsService != null) {
                    try {
                        Students student = studentsService.selectStudentsByPhone(request.getPhone().trim());
                        if (student == null) {
                            Students newStudent = new Students();
                            newStudent.setPhone(request.getPhone().trim());
                            newStudent.setStudentName("待完善"); // 注册时无姓名，insertStudents 要求非空
                            newStudent.setUserType(1); // 1=学员
                            newStudent.setStatus("正常");
                            newStudent.setCreateTime(new Date());
                            newStudent.setUpdateTime(new Date());
                            studentsService.insertStudents(newStudent);
                        }
                    } catch (Exception ex) {
                        // 记录日志但不影响注册结果
                        ex.printStackTrace();
                    }
                }
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
    
    @ApiOperation(value = "获取学员详细信息", notes = "根据手机号获取学员详细信息（来自 students 表）")
    @GetMapping("/studentInfo")
    public AjaxResult getStudentInfo(@RequestParam String phone) {
        try {
            if (studentsService == null) {
                return AjaxResult.error("学员服务未启用");
            }
            Students student = studentsService.selectStudentsByPhone(phone);
            if (student != null) {
                return AjaxResult.success(student);
            } else {
                return AjaxResult.error("未找到学员记录");
            }
        } catch (Exception e) {
            return AjaxResult.error("获取学员信息失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "管理员重置用户密码", notes = "仅管理员可用，用于在学员管理中重置任意用户密码")
    @PostMapping("/adminResetPassword")
    public AjaxResult adminResetPassword(@RequestBody AdminResetPasswordRequest request,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 校验管理员身份
            if (authHeader == null || authHeader.trim().isEmpty()) {
                return AjaxResult.error(401, "未授权：缺少认证信息");
            }
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring("Bearer ".length()).trim() : authHeader.trim();
            String username = loginService.parseUsernameFromToken(token);
            if (username == null || username.trim().isEmpty()) {
                return AjaxResult.error(401, "未授权：Token 无效");
            }

            com.discussio.resourc.model.auto.LoginDiscussionForum adminUser = loginService.getUserInfo(username);
            // userType: 0=普通用户 1=管理员
            if (adminUser == null || adminUser.getUserType() == null || adminUser.getUserType() != 1) {
                return AjaxResult.error(403, "仅管理员可重置用户密码");
            }

            if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
                return AjaxResult.error("手机号不能为空");
            }
            if (request.getNewPassword() == null || request.getNewPassword().trim().isEmpty()) {
                return AjaxResult.error("新密码不能为空");
            }

            boolean success = loginService.adminResetPassword(request.getPhone().trim(), request.getNewPassword());
            if (success) {
                return AjaxResult.success("密码已重置");
            } else {
                return AjaxResult.error("密码重置失败，新密码可能不符合安全要求");
            }
        } catch (Exception e) {
            return AjaxResult.error("密码重置失败：" + e.getMessage());
        }
    }
    
    @ApiOperation(value = "更新学员详细信息", notes = "根据手机号更新学员详细信息（来自 students 表）")
    @PostMapping("/updateStudentInfo")
    public AjaxResult updateStudentInfo(@RequestBody UpdateStudentInfoRequest request) {
        try {
            if (studentsService == null) {
                return AjaxResult.error("学员服务未启用");
            }
            if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
                return AjaxResult.error("手机号不能为空");
            }
            
            Students student = studentsService.selectStudentsByPhone(request.getPhone());
            if (student == null) {
                // 如果记录不存在，创建新记录
                student = new Students();
                student.setPhone(request.getPhone().trim());
                student.setCreateTime(new Date());
                
                // 根据登录用户的 userType 设置 user_type
                com.discussio.resourc.model.auto.LoginDiscussionForum user = loginService.getUserInfo(request.getPhone());
                if (user != null && user.getUserType() != null) {
                    student.setUserType(user.getUserType() == 1 ? 3 : 1); // 1=管理员->3, 0=普通用户->1
                } else {
                    student.setUserType(1); // 默认学员
                }
            }
            
            // 更新字段
            if (request.getStudentName() != null) {
                student.setStudentName(request.getStudentName());
            }
            if (request.getEmail() != null) {
                student.setEmail(request.getEmail());
            }
            if (request.getGender() != null) {
                student.setGender(request.getGender());
            }
            if (request.getBirthDate() != null) {
                student.setBirthDate(request.getBirthDate());
            }
            if (request.getDepartment() != null) {
                student.setDepartment(request.getDepartment());
            }
            if (request.getPosition() != null) {
                student.setPosition(request.getPosition());
            }
            if (request.getTitle() != null) {
                student.setTitle(request.getTitle());
            }
            if (request.getLevel() != null) {
                student.setLevel(request.getLevel());
            }
            if (request.getEmployeeId() != null) {
                student.setEmployeeId(request.getEmployeeId());
            }
            if (request.getUserType() != null) {
                student.setUserType(request.getUserType());
            }
            if (request.getStatus() != null) {
                student.setStatus(request.getStatus());
            }
            if (request.getEnrollmentDate() != null) {
                student.setEnrollmentDate(request.getEnrollmentDate());
            }
            
            student.setUpdateTime(new Date());
            
            int result;
            if (student.getId() == null) {
                result = studentsService.insertStudents(student);
            } else {
                result = studentsService.updateStudents(student);
            }
            
            if (result > 0) {
                return AjaxResult.success("更新成功");
            } else {
                return AjaxResult.error("更新失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("更新学员信息失败：" + e.getMessage());
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

    // 内部类：管理员重置密码请求
    public static class AdminResetPasswordRequest {
        private String phone;
        private String newPassword;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
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
    
    // 内部类：更新学员信息请求
    public static class UpdateStudentInfoRequest {
        private String phone;
        private String studentName;
        private String email;
        private String gender;
        
        @JsonDeserialize(using = MultiFormatDateDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private java.util.Date birthDate;
        
        private String department;
        private String position;
        /** 职称：护士/护师/主管护师/副主任护师/主任护师 */
        private String title;
        /** 层级：N0/N1/N2/N3/N4 */
        private String level;
        private String employeeId;
        private Integer userType;
        private String status;
        
        @JsonDeserialize(using = MultiFormatDateDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
        private java.util.Date enrollmentDate;

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public java.util.Date getBirthDate() { return birthDate; }
        public void setBirthDate(java.util.Date birthDate) { this.birthDate = birthDate; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public Integer getUserType() { return userType; }
        public void setUserType(Integer userType) { this.userType = userType; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public java.util.Date getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(java.util.Date enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    }
}

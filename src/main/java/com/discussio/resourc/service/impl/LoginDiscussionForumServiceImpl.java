package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.LoginDiscussionForumMapper;
import com.discussio.resourc.model.auto.LoginDiscussionForum;
import com.discussio.resourc.model.auto.Students;
import com.discussio.resourc.service.LoginDiscussionForumService;
import com.discussio.resourc.service.IStudentsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录服务实现类
 */
@Service
public class LoginDiscussionForumServiceImpl extends ServiceImpl<LoginDiscussionForumMapper, LoginDiscussionForum> 
        implements LoginDiscussionForumService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginDiscussionForumServiceImpl.class);
    
    private static final int MAX_ATTEMPTS = 2; // 最大登录尝试次数
    private static final int PASSWORD_VALID_DAYS = 30; // 密码有效天数
    
    @Value("${jwt.secret:defaultSecretKey}")
    private String jwtSecret;
    
    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;
    
    @Autowired
    private LoginDiscussionForumMapper loginDiscussionForumMapper;
    
    @Autowired(required = false)
    private IStudentsService studentsService;
    
    // 记录每个用户的登录尝试次数
    private Map<String, Integer> attemptCounts = new HashMap<>();
    // 存储用户名和加密后的密码
    private Map<String, String> userCredentials = new HashMap<>();
    // 记录每个用户最后一次修改密码的日期
    private Map<String, LocalDate> passwordChangeDates = new HashMap<>();

    @Override
    public boolean loginDiscussionForum(String username, String password) {
        try {
            // 参数验证
            if (username == null || username.trim().isEmpty()) {
                logger.warn("登录失败：用户名为空");
                return false;
            }
            if (password == null || password.trim().isEmpty()) {
                logger.warn("登录失败：密码为空 - 用户名: {}", username);
                return false;
            }
            
            // 检查尝试次数
            int attempts = attemptCounts.getOrDefault(username, 0);
            if (attempts >= MAX_ATTEMPTS) {
                logger.warn("账号已锁定 - 用户名: {}, 尝试次数: {}", username, attempts);
                System.out.println("账号已锁定，请稍后再试或联系管理员");
                return false;
            }
            
            // 验证用户是否存在
            LoginDiscussionForum user = loginDiscussionForumMapper.findBydiscussionForumname(username.trim());
            if (user == null) {
                logger.warn("登录失败：用户不存在 - 用户名: {}", username);
                System.out.println("用户名或密码错误");
                attemptCounts.put(username, attempts + 1);
                return false;
            }
            
            // 验证密码（使用SHA-1加密，与注册时保持一致）
            String storedPassword = user.getPassword();
            String inputPassword = encryptSHA1(password);
            
            logger.debug("密码验证 - 用户名: {}, 存储密码: {}, 输入密码加密后: {}", 
                        username, storedPassword, inputPassword);
            
            if (!storedPassword.equals(inputPassword)) {
                logger.warn("密码验证失败 - 用户名: {}", username);
                System.out.println("用户名或密码错误");
                attemptCounts.put(username, attempts + 1);
                return false;
            }
            
            // 检查密码有效期（新注册用户默认有效）
            LocalDate lastChangeDate = passwordChangeDates.getOrDefault(username, LocalDate.now());
            if (lastChangeDate.plusDays(PASSWORD_VALID_DAYS).isBefore(LocalDate.now())) {
                logger.warn("密码已过期 - 用户名: {}", username);
                System.out.println("密码已过期，请修改密码");
                return false;
            }
            
            // 登录成功，重置尝试次数
            attemptCounts.remove(username);
            logger.info("登录成功 - 用户名: {}", username);
            System.out.println("登录成功");
            
            // 检查并创建 students 记录（如果不存在）
            if (studentsService != null) {
                try {
                    Students student = studentsService.selectStudentsByPhone(username.trim());
                    if (student == null) {
                        // 创建新的 students 记录
                        Students newStudent = new Students();
                        newStudent.setPhone(username.trim());
                        newStudent.setStudentName(""); // 待用户完善
                        // 根据 login_discussion_forum 的 userType 映射到 students 的 user_type
                        // userType: 0=普通用户 -> user_type: 1=学员
                        // userType: 1=管理员 -> user_type: 3=管理员
                        Integer userType = user.getUserType();
                        newStudent.setUserType(userType != null && userType == 1 ? 3 : 1);
                        newStudent.setStatus("正常");
                        newStudent.setCreateTime(new Date());
                        newStudent.setUpdateTime(new Date());
                        studentsService.insertStudents(newStudent);
                        logger.info("自动创建学员记录成功 - 手机号: {}", username);
                    } else {
                        // 如果 students 记录存在，同步 user_type（如果不同步）
                        Integer loginUserType = user.getUserType();
                        Integer studentUserType = student.getUserType();
                        if (loginUserType != null) {
                            Integer expectedUserType = loginUserType == 1 ? 3 : 1;
                            if (studentUserType == null || !studentUserType.equals(expectedUserType)) {
                                student.setUserType(expectedUserType);
                                student.setUpdateTime(new Date());
                                studentsService.updateStudents(student);
                                logger.info("同步学员记录用户类型 - 手机号: {}, 类型: {}", username, expectedUserType);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.warn("检查/创建学员记录失败 - 手机号: {}, 错误: {}", username, e.getMessage());
                    // 不影响登录流程，只记录警告
                }
            }
            
            return true;
        } catch (Exception e) {
            logger.error("登录异常 - 用户名: {}, 错误: {}", username, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean registerUser(String username, String password) {
        try {
            // 验证输入
            if (username == null || username.trim().isEmpty()) {
                throw new RuntimeException("用户名不能为空");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new RuntimeException("密码不能为空");
            }
            if (password.length() < 6) {
                throw new RuntimeException("密码长度至少6位");
            }
            
            // 用户名是否已存在
            LoginDiscussionForum existingUser = loginDiscussionForumMapper.findByPaymentschedulename(username);
            if (existingUser != null) {
                logger.warn("注册失败：用户名已存在 - {}", username);
                return false; // 用户名已存在
            }
            
            // 创建新用户并保存
            LoginDiscussionForum newUser = new LoginDiscussionForum();
            newUser.setUsername(username.trim());
            newUser.setPassword(encryptSHA1(password));
            // 默认普通用户；如果这是系统第一个用户，则设置为管理员（方便初始化）
            long userCount = this.count();
            Integer userType = userCount == 0 ? 1 : 0; // 1=管理员, 0=普通用户
            newUser.setUserType(userType);
            boolean saved = this.save(newUser);
            
            if (saved) {
                passwordChangeDates.put(username, LocalDate.now());
                logger.info("用户注册成功 - {}", username);
                
                // 自动创建对应的 students 记录
                if (studentsService != null) {
                    try {
                        Students student = studentsService.selectStudentsByPhone(username.trim());
                        if (student == null) {
                            // 创建新的 students 记录
                            Students newStudent = new Students();
                            newStudent.setPhone(username.trim());
                            newStudent.setStudentName(""); // 待用户完善
                            // 映射 userType: 0=普通用户(学员) -> 1, 1=管理员 -> 3
                            // 但注册时默认是普通用户，所以设为学员(1)
                            // 如果是第一个用户(管理员)，设为管理员(3)
                            newStudent.setUserType(userType == 1 ? 3 : 1); // 1=学员, 3=管理员
                            newStudent.setStatus("正常");
                            newStudent.setCreateTime(new Date());
                            newStudent.setUpdateTime(new Date());
                            studentsService.insertStudents(newStudent);
                            logger.info("自动创建学员记录成功 - 手机号: {}", username);
                        }
                    } catch (Exception e) {
                        logger.warn("自动创建学员记录失败 - 手机号: {}, 错误: {}", username, e.getMessage());
                        // 不影响注册流程，只记录警告
                    }
                }
                
                return true;
            } else {
                logger.error("用户注册失败 - 保存失败");
                return false;
            }
        } catch (Exception e) {
            logger.error("用户注册异常 - {}", e.getMessage(), e);
            throw new RuntimeException("注册失败：" + e.getMessage());
        }
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        // 1. 加载用户信息
        LoginDiscussionForum user = loginDiscussionForumMapper.findBydiscussionForumname(username);
        if (user == null) {
            return false; // 用户不存在
        }
        
        // 2. 验证旧密码是否正确
        if (!user.getPassword().equals(encryptSHA1(oldPassword))) {
            return false; // 旧密码错误
        }
        
        // 3. 检查新密码强度
        if (!isStrongPassword(newPassword)) {
            return false; // 新密码不符合强度要求
        }
        
        // 4. 更新密码（加密存储）
        user.setPassword(encryptSHA1(newPassword));
        this.updateById(user);
        passwordChangeDates.put(username, LocalDate.now());
        return true;
    }

    @Override
    public String generateToken(String username) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
            
            // 使用HS256算法（更安全且密钥要求更低）
            // 如果密钥长度不足，自动扩展
            String secretKey = ensureKeyLength(jwtSecret);
            
            return Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        } catch (Exception e) {
            logger.error("生成Token失败 - 用户名: {}, 错误: {}", username, e.getMessage(), e);
            throw new RuntimeException("生成Token失败：" + e.getMessage());
        }
    }
    
    /**
     * 确保密钥长度足够（HS256至少需要256位，即32个字符）
     */
    private String ensureKeyLength(String key) {
        if (key == null || key.length() < 32) {
            // 如果密钥太短，使用SHA-256哈希扩展
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(key.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    hexString.append(String.format("%02x", b));
                }
                // 重复一次以确保长度足够
                String hashed = hexString.toString();
                return hashed + hashed.substring(0, 32);
            } catch (NoSuchAlgorithmException e) {
                // 如果哈希失败，使用默认长密钥
                return "SurgiLearnSystemSecretKeyForJWTTokenGeneration2024SecureKey";
            }
        }
        return key;
    }

    /**
     * 使用SHA-256算法对密码进行加密
     */
    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用", e);
        }
    }

    /**
     * 使用SHA-1算法对密码进行加密
     */
    private String encryptSHA1(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("加密算法不可用", e);
        }
    }

    /**
     * 检查密码强度
     */
    private boolean isStrongPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        // 简单检查：至少包含数字和字母
        boolean hasDigit = false;
        boolean hasLetter = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (Character.isLetter(c)) {
                hasLetter = true;
            }
        }
        return hasDigit && hasLetter;
    }
    
    @Override
    public LoginDiscussionForum getUserInfo(String username) {
        try {
            return loginDiscussionForumMapper.findBydiscussionForumname(username);
        } catch (Exception e) {
            logger.error("获取用户信息失败 - 用户名: {}, 错误: {}", username, e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public boolean updateUserInfo(String username, String nickname) {
        try {
            LoginDiscussionForum user = loginDiscussionForumMapper.findBydiscussionForumname(username);
            if (user == null) {
                logger.warn("更新用户信息失败：用户不存在 - 用户名: {}", username);
                return false;
            }
            user.setNickname(nickname);
            boolean updated = this.updateById(user);
            if (updated) {
                logger.info("用户信息更新成功 - 用户名: {}, 昵称: {}", username, nickname);
            }
            return updated;
        } catch (Exception e) {
            logger.error("更新用户信息失败 - 用户名: {}, 错误: {}", username, e.getMessage(), e);
            return false;
        }
    }
}

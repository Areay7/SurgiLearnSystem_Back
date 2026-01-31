-- ============================================
-- 外科护理主管护师培训学习系统 - 完整数据库初始化脚本
-- ============================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS surgilearn DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 使用数据库
USE surgilearn;

-- ============================================
-- 基础功能模块表
-- ============================================

-- 登录讨论论坛表
CREATE TABLE IF NOT EXISTS `login_discussion_forum` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL COMMENT '用户名（手机号）',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
  `user_type` INT(11) DEFAULT 0 COMMENT '用户类型 0-普通用户 1-管理员',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录用户表';

-- 讨论论坛模块表
CREATE TABLE IF NOT EXISTS `discussion_forum` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `discussion_id` BIGINT(20) DEFAULT NULL COMMENT '讨论ID',
  `forum_title` VARCHAR(300) DEFAULT NULL COMMENT '论坛标题',
  `poster_id` VARCHAR(150) DEFAULT NULL COMMENT '发帖者ID',
  `post_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `content` TEXT COMMENT '讨论内容',
  `reply_count` INT(11) DEFAULT 0 COMMENT '回复数量',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞数量',
  `is_sticky` INT(11) DEFAULT 0 COMMENT '是否置顶 0-否 1-是',
  `is_locked` INT(11) DEFAULT 0 COMMENT '是否锁定 0-否 1-是',
  `last_reply_id` VARCHAR(100) DEFAULT NULL COMMENT '最后回复ID',
  `last_reply_time` DATETIME DEFAULT NULL COMMENT '最后回复时间',
  `category_id` VARCHAR(100) DEFAULT NULL COMMENT '分类ID',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_post_time` (`post_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论论坛表';

-- 讨论论坛回复表
CREATE TABLE IF NOT EXISTS `forum_reply` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `reply_id` BIGINT(20) DEFAULT NULL COMMENT '回复ID',
  `forum_id` BIGINT(20) NOT NULL COMMENT '所属话题ID',
  `replier_id` VARCHAR(150) DEFAULT NULL COMMENT '回复者ID',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `content` TEXT COMMENT '回复内容',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞数量',
  `parent_reply_id` BIGINT(20) DEFAULT NULL COMMENT '父回复ID（用于回复的回复）',
  `is_deleted` INT(11) DEFAULT 0 COMMENT '是否删除 0-否 1-是',
  PRIMARY KEY (`id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_reply_time` (`reply_time`),
  KEY `idx_parent_reply_id` (`parent_reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论论坛回复表';

-- 资源共享平台表
CREATE TABLE IF NOT EXISTS `resource_sharing` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `resource_id` BIGINT(20) DEFAULT NULL COMMENT '资源ID',
  `resource_name` VARCHAR(160) DEFAULT NULL COMMENT '资源名称',
  `resource_type` VARCHAR(210) DEFAULT NULL COMMENT '资源类型',
  `upload_date` DATETIME DEFAULT NULL COMMENT '上传日期',
  `upload_user` VARCHAR(140) DEFAULT NULL COMMENT '上传用户',
  `download_count` INT(11) DEFAULT 0 COMMENT '下载次数',
  `resource_desc` TEXT COMMENT '资源描述',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
  `is_approved` INT(11) DEFAULT 0 COMMENT '是否通过 0-待审核 1-已通过 2-已拒绝',
  `approval_date` DATETIME DEFAULT NULL COMMENT '通过日期',
  PRIMARY KEY (`id`),
  KEY `idx_resource_type` (`resource_type`),
  KEY `idx_upload_date` (`upload_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源共享平台表';

-- 反馈评价模块表
CREATE TABLE IF NOT EXISTS `feedback_module` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `module_id` BIGINT(20) DEFAULT NULL COMMENT '模块ID',
  `module_name` VARCHAR(190) DEFAULT NULL COMMENT '模块名称',
  `module_type` VARCHAR(170) DEFAULT NULL COMMENT '模块类型',
  `parent_module_id` VARCHAR(280) DEFAULT NULL COMMENT '父模块ID',
  `sort_order` VARCHAR(290) DEFAULT NULL COMMENT '排序顺序',
  `is_active` INT(11) DEFAULT 1 COMMENT '是否启用 0-否 1-是',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` TEXT COMMENT '备注信息',
  `icon_url` VARCHAR(500) DEFAULT NULL COMMENT '图标链接',
  PRIMARY KEY (`id`),
  KEY `idx_module_type` (`module_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='反馈评价模块表';

-- 证书颁发功能表
CREATE TABLE IF NOT EXISTS `certificate_issue` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `certificate_id` BIGINT(20) DEFAULT NULL COMMENT '证书ID',
  `issue_date` DATETIME DEFAULT NULL COMMENT '颁发日期',
  `certificate_type` VARCHAR(250) DEFAULT NULL COMMENT '证书类型',
  `holder_name` VARCHAR(180) DEFAULT NULL COMMENT '持证人姓名',
  `holder_id` VARCHAR(100) DEFAULT NULL COMMENT '持证人ID',
  `training_course` VARCHAR(200) DEFAULT NULL COMMENT '培训课程',
  `organization` VARCHAR(200) DEFAULT NULL COMMENT '颁发机构',
  `expiry_date` DATETIME DEFAULT NULL COMMENT '到期日期',
  `certificate_status` VARCHAR(50) DEFAULT NULL COMMENT '证书状态',
  `issue_note` TEXT COMMENT '颁发备注',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_certificate_type` (`certificate_type`),
  KEY `idx_certificate_status` (`certificate_status`),
  KEY `idx_holder_id` (`holder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='证书颁发功能表';

-- 移动访问支持表
CREATE TABLE IF NOT EXISTS `mobile_access` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `access_id` BIGINT(20) DEFAULT NULL COMMENT '访问ID',
  `user_id` VARCHAR(220) DEFAULT NULL COMMENT '用户ID',
  `device_type` VARCHAR(100) DEFAULT NULL COMMENT '设备类型',
  `device_model` VARCHAR(100) DEFAULT NULL COMMENT '设备型号',
  `os_version` VARCHAR(100) DEFAULT NULL COMMENT '操作系统版本',
  `app_version` VARCHAR(100) DEFAULT NULL COMMENT '应用版本',
  `access_time` DATETIME DEFAULT NULL COMMENT '访问时间',
  `ip_address` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '访问地点',
  `access_status` VARCHAR(50) DEFAULT NULL COMMENT '访问状态',
  `error_code` VARCHAR(50) DEFAULT NULL COMMENT '错误代码',
  `error_msg` TEXT COMMENT '错误信息',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_device_type` (`device_type`),
  KEY `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='移动访问支持表';

-- 系统设置选项表
CREATE TABLE IF NOT EXISTS `system_settings` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `settings_id` BIGINT(20) DEFAULT NULL COMMENT '设置ID',
  `course_type` VARCHAR(100) DEFAULT NULL COMMENT '课程类型',
  `learning_mode` VARCHAR(100) DEFAULT NULL COMMENT '学习模式',
  `exam_time_limit` VARCHAR(100) DEFAULT NULL COMMENT '考试时限',
  `video_quality` VARCHAR(100) DEFAULT NULL COMMENT '视频质量',
  `question_bank_type` VARCHAR(100) DEFAULT NULL COMMENT '题库类型',
  `update_frequency` VARCHAR(100) DEFAULT NULL COMMENT '更新频率',
  `certificate_type` VARCHAR(100) DEFAULT NULL COMMENT '证书类型',
  `supported_languages` VARCHAR(200) DEFAULT NULL COMMENT '支持语言',
  `customer_service_email` VARCHAR(200) DEFAULT NULL COMMENT '客服邮箱',
  `system_version` VARCHAR(50) DEFAULT NULL COMMENT '系统版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置选项表';

-- 用户权限管理表
CREATE TABLE IF NOT EXISTS `user_permission` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户ID(students.id)',
  `user_phone` VARCHAR(20) DEFAULT NULL COMMENT '用户手机号',
  `permission_code` VARCHAR(100) DEFAULT NULL COMMENT '权限代码',
  `permission_name` VARCHAR(200) DEFAULT NULL COMMENT '权限名称',
  `is_active` INT(11) DEFAULT 1 COMMENT '是否启用 0-否 1-是',
  `grant_type` VARCHAR(20) DEFAULT 'grant' COMMENT 'grant-授予 revoke-收回',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_phone` (`user_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户权限管理表';

-- 权限定义表
CREATE TABLE IF NOT EXISTS `permission_def` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限代码',
  `permission_name` VARCHAR(200) NOT NULL COMMENT '权限名称',
  `module` VARCHAR(80) DEFAULT NULL COMMENT '所属模块',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `sort_order` INT(11) DEFAULT 0,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限定义表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色代码',
  `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `user_type_flag` INT(11) DEFAULT NULL COMMENT '对应用户类型',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT(20) NOT NULL,
  `permission_code` VARCHAR(100) NOT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_code`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ============================================
-- 教学管理模块表
-- ============================================

-- 护理培训表
CREATE TABLE IF NOT EXISTS `training` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) DEFAULT NULL COMMENT '培训ID',
  `training_name` VARCHAR(200) DEFAULT NULL COMMENT '培训名称',
  `training_type` VARCHAR(100) DEFAULT NULL COMMENT '培训类型',
  `description` TEXT COMMENT '培训描述',
  `start_date` DATETIME DEFAULT NULL COMMENT '开始日期',
  `end_date` DATETIME DEFAULT NULL COMMENT '结束日期',
  `instructor_id` VARCHAR(100) DEFAULT NULL COMMENT '讲师ID',
  `instructor_name` VARCHAR(100) DEFAULT NULL COMMENT '讲师姓名',
  `max_participants` INT(11) DEFAULT 0 COMMENT '最大参与人数',
  `current_participants` INT(11) DEFAULT 0 COMMENT '当前参与人数',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_training_type` (`training_type`),
  KEY `idx_start_date` (`start_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理培训表';

-- 学习资料库（培训资料白板引用）
CREATE TABLE IF NOT EXISTS `learning_materials` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` VARCHAR(1000) DEFAULT NULL COMMENT '描述',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '分类',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签',
  `file_type` VARCHAR(20) DEFAULT NULL COMMENT '文件类型后缀',
  `file_size` BIGINT(20) DEFAULT NULL COMMENT '文件大小',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件存储路径',
  `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  `uploader_id` VARCHAR(100) DEFAULT NULL COMMENT '上传人ID',
  `uploader_name` VARCHAR(100) DEFAULT NULL COMMENT '上传人姓名',
  `view_count` INT(11) DEFAULT 0,
  `download_count` INT(11) DEFAULT 0,
  `status` VARCHAR(50) DEFAULT '已发布',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资料管理表';

-- 培训-资料关联表
CREATE TABLE IF NOT EXISTS `training_material` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL,
  `material_id` BIGINT(20) NOT NULL,
  `sort_order` INT(11) DEFAULT 0,
  `required` TINYINT(1) DEFAULT 1,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_training_id` (`training_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训-资料关联';

-- 培训进度表
CREATE TABLE IF NOT EXISTS `training_progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL,
  `student_id` BIGINT(20) NOT NULL,
  `student_name` VARCHAR(100) DEFAULT NULL,
  `progress_percent` INT(11) DEFAULT 0,
  `completed_count` INT(11) DEFAULT 0,
  `total_count` INT(11) DEFAULT 0,
  `status` VARCHAR(50) DEFAULT NULL,
  `last_study_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_student` (`training_id`, `student_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训进度';

-- 培训资料完成明细表
CREATE TABLE IF NOT EXISTS `training_material_progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL,
  `material_id` BIGINT(20) NOT NULL,
  `student_id` BIGINT(20) NOT NULL,
  `progress_percent` INT(11) DEFAULT 0,
  `completed` TINYINT(1) DEFAULT 0,
  `last_position` INT(11) DEFAULT 0,
  `last_study_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tmp` (`training_id`, `material_id`, `student_id`),
  KEY `idx_training_material` (`training_id`, `material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训资料完成明细';

-- 培训资料白板内容块表
CREATE TABLE IF NOT EXISTS `training_content_blocks` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL,
  `block_type` VARCHAR(20) NOT NULL,
  `sort_order` INT(11) DEFAULT 0,
  `content` TEXT DEFAULT NULL,
  `material_id` BIGINT(20) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_training_id` (`training_id`),
  KEY `idx_sort` (`training_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训资料白板内容块';

-- 培训内容块进度表
CREATE TABLE IF NOT EXISTS `training_content_block_progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL,
  `block_id` BIGINT(20) NOT NULL,
  `student_id` BIGINT(20) NOT NULL,
  `block_type` VARCHAR(20) NOT NULL,
  `viewed` TINYINT(1) DEFAULT 0,
  `view_duration` INT(11) DEFAULT 0,
  `play_progress` INT(11) DEFAULT 0,
  `scroll_progress` INT(11) DEFAULT 0,
  `downloaded` TINYINT(1) DEFAULT 0,
  `first_view_time` DATETIME DEFAULT NULL,
  `last_view_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_block_student` (`block_id`, `student_id`),
  KEY `idx_training_student` (`training_id`, `student_id`),
  KEY `idx_block_id` (`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训内容块进度';

-- 课程资料表（schedule 等模块使用）
CREATE TABLE IF NOT EXISTS `materials` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `material_id` BIGINT(20) DEFAULT NULL COMMENT '资料ID',
  `material_name` VARCHAR(200) DEFAULT NULL COMMENT '资料名称',
  `material_type` VARCHAR(100) DEFAULT NULL COMMENT '资料类型',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件路径',
  `file_size` VARCHAR(50) DEFAULT NULL COMMENT '文件大小',
  `description` TEXT COMMENT '描述',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '分类',
  `upload_user_id` VARCHAR(100) DEFAULT NULL COMMENT '上传用户ID',
  `upload_user_name` VARCHAR(100) DEFAULT NULL COMMENT '上传用户姓名',
  `upload_time` DATETIME DEFAULT NULL COMMENT '上传时间',
  `download_count` INT(11) DEFAULT 0 COMMENT '下载次数',
  `view_count` INT(11) DEFAULT 0 COMMENT '查看次数',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_material_type` (`material_type`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资料管理表';

-- 课程安排设置表
CREATE TABLE IF NOT EXISTS `schedule` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `schedule_id` BIGINT(20) DEFAULT NULL COMMENT '安排ID',
  `course_name` VARCHAR(200) DEFAULT NULL COMMENT '课程名称',
  `course_type` VARCHAR(100) DEFAULT NULL COMMENT '课程类型',
  `schedule_date` DATE DEFAULT NULL COMMENT '安排日期',
  `start_time` VARCHAR(20) DEFAULT NULL COMMENT '开始时间',
  `end_time` VARCHAR(20) DEFAULT NULL COMMENT '结束时间',
  `classroom` VARCHAR(100) DEFAULT NULL COMMENT '教室',
  `instructor_id` VARCHAR(100) DEFAULT NULL COMMENT '讲师ID',
  `instructor_name` VARCHAR(100) DEFAULT NULL COMMENT '讲师姓名',
  `max_students` INT(11) DEFAULT 0 COMMENT '最大学生数',
  `enrolled_students` INT(11) DEFAULT 0 COMMENT '已报名学生数',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `description` TEXT COMMENT '描述',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_date` (`schedule_date`),
  KEY `idx_instructor_id` (`instructor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程安排设置表';

-- 视频讲座播放表
CREATE TABLE IF NOT EXISTS `videos` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `video_id` BIGINT(20) DEFAULT NULL COMMENT '视频ID',
  `video_title` VARCHAR(200) DEFAULT NULL COMMENT '视频标题',
  `video_url` VARCHAR(500) DEFAULT NULL COMMENT '视频URL',
  `video_type` VARCHAR(100) DEFAULT NULL COMMENT '视频类型',
  `description` TEXT COMMENT '描述',
  `instructor_id` VARCHAR(100) DEFAULT NULL COMMENT '讲师ID',
  `instructor_name` VARCHAR(100) DEFAULT NULL COMMENT '讲师姓名',
  `duration` INT(11) DEFAULT 0 COMMENT '时长（秒）',
  `thumbnail_url` VARCHAR(500) DEFAULT NULL COMMENT '缩略图URL',
  `view_count` INT(11) DEFAULT 0 COMMENT '观看次数',
  `like_count` INT(11) DEFAULT 0 COMMENT '点赞次数',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_type` (`video_type`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频讲座播放表';

-- 视频收藏表
CREATE TABLE IF NOT EXISTS `video_favorites` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(100) DEFAULT NULL COMMENT '用户ID（手机号）',
  `video_id` BIGINT(20) DEFAULT NULL COMMENT '视频ID',
  `create_time` DATETIME DEFAULT NULL COMMENT '收藏时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_video_id` (`video_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='视频收藏表';

-- 在线题库表
CREATE TABLE IF NOT EXISTS `question_bank` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `question_id` BIGINT(20) DEFAULT NULL COMMENT '题目ID',
  `question_type` VARCHAR(50) DEFAULT NULL COMMENT '题目类型',
  `question_content` TEXT COMMENT '题目内容',
  `option_a` VARCHAR(500) DEFAULT NULL COMMENT '选项A',
  `option_b` VARCHAR(500) DEFAULT NULL COMMENT '选项B',
  `option_c` VARCHAR(500) DEFAULT NULL COMMENT '选项C',
  `option_d` VARCHAR(500) DEFAULT NULL COMMENT '选项D',
  `correct_answer` VARCHAR(10) DEFAULT NULL COMMENT '正确答案',
  `explanation` TEXT COMMENT '解析',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '分类',
  `difficulty` VARCHAR(50) DEFAULT NULL COMMENT '难度',
  `score` INT(11) DEFAULT 0 COMMENT '分值',
  `creator_id` VARCHAR(100) DEFAULT NULL COMMENT '创建者ID',
  `creator_name` VARCHAR(100) DEFAULT NULL COMMENT '创建者姓名',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_question_type` (`question_type`),
  KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='在线题库表';

-- 考试系统表
CREATE TABLE IF NOT EXISTS `exam` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `exam_id` BIGINT(20) DEFAULT NULL COMMENT '考试ID',
  `exam_name` VARCHAR(200) DEFAULT NULL COMMENT '考试名称',
  `exam_type` VARCHAR(100) DEFAULT NULL COMMENT '考试类型',
  `exam_date` DATE DEFAULT NULL COMMENT '考试日期',
  `start_time` VARCHAR(20) DEFAULT NULL COMMENT '开始时间',
  `end_time` VARCHAR(20) DEFAULT NULL COMMENT '结束时间',
  `duration` INT(11) DEFAULT 0 COMMENT '时长（分钟）',
  `total_score` INT(11) DEFAULT 0 COMMENT '总分',
  `pass_score` INT(11) DEFAULT 0 COMMENT '及格分',
  `question_ids` TEXT COMMENT '题目ID列表（JSON格式）',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `creator_id` VARCHAR(100) DEFAULT NULL COMMENT '创建者ID',
  `creator_name` VARCHAR(100) DEFAULT NULL COMMENT '创建者姓名',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_date` (`exam_date`),
  KEY `idx_exam_type` (`exam_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试系统表';

-- 培训-班级关联表（空=全员可见，有记录=仅指定班级可见）
CREATE TABLE IF NOT EXISTS `training_class` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL COMMENT '培训ID',
  `class_id` BIGINT(20) NOT NULL COMMENT '班级ID',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_class` (`training_id`, `class_id`),
  KEY `idx_training_id` (`training_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训-班级关联表';

-- 考试-班级关联表（空=全员可见，有记录=仅指定班级可见）
CREATE TABLE IF NOT EXISTS `exam_class` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `exam_id` BIGINT(20) NOT NULL COMMENT '考试ID',
  `class_id` BIGINT(20) NOT NULL COMMENT '班级ID',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_class` (`exam_id`, `class_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试-班级关联表';

-- 学员记录管理表
CREATE TABLE IF NOT EXISTS `students` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT(20) DEFAULT NULL COMMENT '学员ID',
  `student_name` VARCHAR(100) DEFAULT NULL COMMENT '学员姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '部门',
  `position` VARCHAR(100) DEFAULT NULL COMMENT '职位',
  `employee_id` VARCHAR(50) DEFAULT NULL COMMENT '员工编号',
  `user_type` INT(11) DEFAULT 1 COMMENT '用户类型 1-学员 2-讲师 3-其他(管理员)',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `enrollment_date` DATE DEFAULT NULL COMMENT '入学日期',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_department` (`department`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员记录管理表';

-- 班级管理表
CREATE TABLE IF NOT EXISTS `teaching_class` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `class_code` VARCHAR(50) DEFAULT NULL COMMENT '班级编码',
  `class_name` VARCHAR(200) NOT NULL COMMENT '班级名称',
  `description` TEXT DEFAULT NULL COMMENT '班级描述',
  `status` VARCHAR(50) DEFAULT '正常' COMMENT '状态',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_name` (`class_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

CREATE TABLE IF NOT EXISTS `teaching_class_instructor` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `class_id` BIGINT(20) NOT NULL,
  `student_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_instructor` (`class_id`, `student_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级讲师关联表';

CREATE TABLE IF NOT EXISTS `teaching_class_student` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `class_id` BIGINT(20) NOT NULL,
  `student_id` BIGINT(20) NOT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_student` (`class_id`, `student_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级学员关联表';

-- 学习进度跟踪表
CREATE TABLE IF NOT EXISTS `progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `progress_id` BIGINT(20) DEFAULT NULL COMMENT '进度ID',
  `student_id` BIGINT(20) DEFAULT NULL COMMENT '学员ID',
  `student_name` VARCHAR(100) DEFAULT NULL COMMENT '学员姓名',
  `course_id` BIGINT(20) DEFAULT NULL COMMENT '课程ID',
  `course_name` VARCHAR(200) DEFAULT NULL COMMENT '课程名称',
  `progress_percent` INT(11) DEFAULT 0 COMMENT '进度百分比',
  `completed_lessons` INT(11) DEFAULT 0 COMMENT '已完成课时',
  `total_lessons` INT(11) DEFAULT 0 COMMENT '总课时',
  `last_study_time` DATETIME DEFAULT NULL COMMENT '最后学习时间',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习进度跟踪表';

-- 讲师分配表
CREATE TABLE IF NOT EXISTS `instructors` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `instructor_id` BIGINT(20) DEFAULT NULL COMMENT '讲师ID',
  `instructor_name` VARCHAR(100) DEFAULT NULL COMMENT '讲师姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '部门',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '职称',
  `specialty` VARCHAR(200) DEFAULT NULL COMMENT '专业领域',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_department` (`department`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讲师分配表';

-- ============================================
-- 备份配置与记录表
-- ============================================
CREATE TABLE IF NOT EXISTS `backup_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `backup_path` VARCHAR(500) DEFAULT NULL COMMENT '备份文件保存路径',
  `auto_enabled` INT(11) DEFAULT 0 COMMENT '自动备份是否启用 0-否 1-是',
  `schedule_cron` VARCHAR(100) DEFAULT '0 0 2 * * ?' COMMENT 'Cron表达式，默认每日2点',
  `schedule_time` VARCHAR(20) DEFAULT '02:00' COMMENT '简易时间 HH:mm（用于显示）',
  `retention_days` INT(11) DEFAULT 30 COMMENT '备份保留天数',
  `include_uploads` INT(11) DEFAULT 1 COMMENT '是否包含uploads文件夹 0-否 1-是',
  `include_database` INT(11) DEFAULT 1 COMMENT '是否包含数据库导出 0-否 1-是',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份配置表';

CREATE TABLE IF NOT EXISTS `backup_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(255) DEFAULT NULL COMMENT '备份文件名',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '备份文件完整路径',
  `file_size` BIGINT(20) DEFAULT 0 COMMENT '文件大小（字节）',
  `backup_type` VARCHAR(20) DEFAULT 'manual' COMMENT 'manual-手动 auto-自动',
  `status` VARCHAR(20) DEFAULT 'success' COMMENT 'success-成功 failed-失败',
  `error_msg` TEXT COMMENT '失败时错误信息',
  `duration_seconds` INT(11) DEFAULT 0 COMMENT '备份耗时（秒）',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份记录表';

-- 初始化默认备份配置
INSERT IGNORE INTO `backup_config` (`id`, `backup_path`, `auto_enabled`, `schedule_cron`, `schedule_time`, `retention_days`, `include_uploads`, `include_database`, `create_time`, `update_time`)
VALUES (1, NULL, 0, '0 0 2 * * ?', '02:00', 30, 1, 1, NOW(), NOW());

-- ============================================
-- 初始化数据（可选）
-- ============================================

-- 插入默认管理员账号（密码：admin123，实际使用时请修改）
-- INSERT INTO `login_discussion_forum` (`username`, `password`) 
-- VALUES ('admin', 'e10adc3949ba59abbe56e057f20f883e'); -- 密码：123456的MD5值

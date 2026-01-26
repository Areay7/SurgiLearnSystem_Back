-- 外科护理主管护师培训学习系统数据库初始化脚本

CREATE DATABASE IF NOT EXISTS surgilearn DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE surgilearn;

-- 登录讨论论坛表
CREATE TABLE IF NOT EXISTS `login_discussion_forum` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 讨论论坛模块表
CREATE TABLE IF NOT EXISTS `discussion_forum` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `discussion_id` BIGINT(20) DEFAULT NULL,
  `forum_title` VARCHAR(300) DEFAULT NULL,
  `poster_id` VARCHAR(150) DEFAULT NULL,
  `post_time` DATETIME DEFAULT NULL,
  `content` TEXT,
  `reply_count` INT(11) DEFAULT 0,
  `like_count` INT(11) DEFAULT 0,
  `is_sticky` INT(11) DEFAULT 0,
  `is_locked` INT(11) DEFAULT 0,
  `last_reply_id` VARCHAR(100) DEFAULT NULL,
  `last_reply_time` DATETIME DEFAULT NULL,
  `category_id` VARCHAR(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 讨论论坛回复表
CREATE TABLE IF NOT EXISTS `forum_reply` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `reply_id` BIGINT(20) DEFAULT NULL,
  `forum_id` BIGINT(20) NOT NULL,
  `replier_id` VARCHAR(150) DEFAULT NULL,
  `reply_time` DATETIME DEFAULT NULL,
  `content` TEXT,
  `like_count` INT(11) DEFAULT 0,
  `parent_reply_id` BIGINT(20) DEFAULT NULL,
  `is_deleted` INT(11) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_forum_id` (`forum_id`),
  KEY `idx_reply_time` (`reply_time`),
  KEY `idx_parent_reply_id` (`parent_reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资源共享平台表
CREATE TABLE IF NOT EXISTS `resource_sharing` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `resource_id` BIGINT(20) DEFAULT NULL,
  `resource_name` VARCHAR(160) DEFAULT NULL,
  `resource_type` VARCHAR(210) DEFAULT NULL,
  `upload_date` DATETIME DEFAULT NULL,
  `upload_user` VARCHAR(140) DEFAULT NULL,
  `download_count` INT(11) DEFAULT 0,
  `resource_desc` TEXT,
  `file_path` VARCHAR(500) DEFAULT NULL,
  `is_approved` INT(11) DEFAULT 0,
  `approval_date` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 反馈评价模块表
CREATE TABLE IF NOT EXISTS `feedback_module` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `module_id` BIGINT(20) DEFAULT NULL,
  `module_name` VARCHAR(190) DEFAULT NULL,
  `module_type` VARCHAR(170) DEFAULT NULL,
  `parent_module_id` VARCHAR(280) DEFAULT NULL,
  `sort_order` VARCHAR(290) DEFAULT NULL,
  `is_active` INT(11) DEFAULT 1,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  `remark` TEXT,
  `icon_url` VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 证书颁发功能表
CREATE TABLE IF NOT EXISTS `certificate_issue` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `certificate_id` BIGINT(20) DEFAULT NULL,
  `issue_date` DATETIME DEFAULT NULL,
  `certificate_type` VARCHAR(250) DEFAULT NULL,
  `holder_name` VARCHAR(180) DEFAULT NULL,
  `holder_id` VARCHAR(100) DEFAULT NULL,
  `training_course` VARCHAR(200) DEFAULT NULL,
  `organization` VARCHAR(200) DEFAULT NULL,
  `expiry_date` DATETIME DEFAULT NULL,
  `certificate_status` VARCHAR(50) DEFAULT NULL,
  `issue_note` TEXT,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 移动访问支持表
CREATE TABLE IF NOT EXISTS `mobile_access` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `access_id` BIGINT(20) DEFAULT NULL,
  `user_id` VARCHAR(220) DEFAULT NULL,
  `device_type` VARCHAR(100) DEFAULT NULL,
  `device_model` VARCHAR(100) DEFAULT NULL,
  `os_version` VARCHAR(100) DEFAULT NULL,
  `app_version` VARCHAR(100) DEFAULT NULL,
  `access_time` DATETIME DEFAULT NULL,
  `ip_address` VARCHAR(50) DEFAULT NULL,
  `location` VARCHAR(200) DEFAULT NULL,
  `access_status` VARCHAR(50) DEFAULT NULL,
  `error_code` VARCHAR(50) DEFAULT NULL,
  `error_msg` TEXT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 系统设置选项表
CREATE TABLE IF NOT EXISTS `system_settings` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `settings_id` BIGINT(20) DEFAULT NULL,
  `course_type` VARCHAR(100) DEFAULT NULL,
  `learning_mode` VARCHAR(100) DEFAULT NULL,
  `exam_time_limit` VARCHAR(100) DEFAULT NULL,
  `video_quality` VARCHAR(100) DEFAULT NULL,
  `question_bank_type` VARCHAR(100) DEFAULT NULL,
  `update_frequency` VARCHAR(100) DEFAULT NULL,
  `certificate_type` VARCHAR(100) DEFAULT NULL,
  `supported_languages` VARCHAR(200) DEFAULT NULL,
  `customer_service_email` VARCHAR(200) DEFAULT NULL,
  `system_version` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户权限管理表
CREATE TABLE IF NOT EXISTS `user_permission` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL,
  `permission_code` VARCHAR(100) DEFAULT NULL,
  `permission_name` VARCHAR(200) DEFAULT NULL,
  `is_active` INT(11) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

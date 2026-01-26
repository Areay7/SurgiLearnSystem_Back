-- 教学管理模块数据库表

USE surgilearn;

-- 护理培训表
CREATE TABLE IF NOT EXISTS `training` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) DEFAULT NULL,
  `training_name` VARCHAR(200) DEFAULT NULL,
  `training_type` VARCHAR(100) DEFAULT NULL,
  `description` TEXT,
  `start_date` DATETIME DEFAULT NULL,
  `end_date` DATETIME DEFAULT NULL,
  `instructor_id` VARCHAR(100) DEFAULT NULL,
  `instructor_name` VARCHAR(100) DEFAULT NULL,
  `max_participants` INT(11) DEFAULT 0,
  `current_participants` INT(11) DEFAULT 0,
  `status` VARCHAR(50) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 学习资料管理表
CREATE TABLE IF NOT EXISTS `materials` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `material_id` BIGINT(20) DEFAULT NULL,
  `material_name` VARCHAR(200) DEFAULT NULL,
  `material_type` VARCHAR(100) DEFAULT NULL,
  `file_path` VARCHAR(500) DEFAULT NULL,
  `file_size` VARCHAR(50) DEFAULT NULL,
  `description` TEXT,
  `category` VARCHAR(100) DEFAULT NULL,
  `upload_user_id` VARCHAR(100) DEFAULT NULL,
  `upload_user_name` VARCHAR(100) DEFAULT NULL,
  `upload_time` DATETIME DEFAULT NULL,
  `download_count` INT(11) DEFAULT 0,
  `view_count` INT(11) DEFAULT 0,
  `status` VARCHAR(50) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 课程安排设置表
CREATE TABLE IF NOT EXISTS `schedule` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `schedule_id` BIGINT(20) DEFAULT NULL,
  `course_name` VARCHAR(200) DEFAULT NULL,
  `course_type` VARCHAR(100) DEFAULT NULL,
  `schedule_date` DATE DEFAULT NULL,
  `start_time` VARCHAR(20) DEFAULT NULL,
  `end_time` VARCHAR(20) DEFAULT NULL,
  `classroom` VARCHAR(100) DEFAULT NULL,
  `instructor_id` VARCHAR(100) DEFAULT NULL,
  `instructor_name` VARCHAR(100) DEFAULT NULL,
  `max_students` INT(11) DEFAULT 0,
  `enrolled_students` INT(11) DEFAULT 0,
  `status` VARCHAR(50) DEFAULT NULL,
  `description` TEXT,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 视频讲座播放表
CREATE TABLE IF NOT EXISTS `videos` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `video_id` BIGINT(20) DEFAULT NULL,
  `video_title` VARCHAR(200) DEFAULT NULL,
  `video_url` VARCHAR(500) DEFAULT NULL,
  `video_type` VARCHAR(100) DEFAULT NULL,
  `description` TEXT,
  `instructor_id` VARCHAR(100) DEFAULT NULL,
  `instructor_name` VARCHAR(100) DEFAULT NULL,
  `duration` INT(11) DEFAULT 0,
  `thumbnail_url` VARCHAR(500) DEFAULT NULL,
  `view_count` INT(11) DEFAULT 0,
  `like_count` INT(11) DEFAULT 0,
  `status` VARCHAR(50) DEFAULT NULL,
  `publish_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 在线题库表
CREATE TABLE IF NOT EXISTS `question_bank` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `question_id` BIGINT(20) DEFAULT NULL,
  `question_type` VARCHAR(50) DEFAULT NULL,
  `question_content` TEXT,
  `option_a` VARCHAR(500) DEFAULT NULL,
  `option_b` VARCHAR(500) DEFAULT NULL,
  `option_c` VARCHAR(500) DEFAULT NULL,
  `option_d` VARCHAR(500) DEFAULT NULL,
  `correct_answer` VARCHAR(10) DEFAULT NULL,
  `explanation` TEXT,
  `category` VARCHAR(100) DEFAULT NULL,
  `difficulty` VARCHAR(50) DEFAULT NULL,
  `score` INT(11) DEFAULT 0,
  `creator_id` VARCHAR(100) DEFAULT NULL,
  `creator_name` VARCHAR(100) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 考试系统表
CREATE TABLE IF NOT EXISTS `exam` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `exam_id` BIGINT(20) DEFAULT NULL,
  `exam_name` VARCHAR(200) DEFAULT NULL,
  `exam_type` VARCHAR(100) DEFAULT NULL,
  `exam_date` DATE DEFAULT NULL,
  `start_time` VARCHAR(20) DEFAULT NULL,
  `end_time` VARCHAR(20) DEFAULT NULL,
  `duration` INT(11) DEFAULT 0,
  `total_score` INT(11) DEFAULT 0,
  `pass_score` INT(11) DEFAULT 0,
  `question_ids` TEXT,
  `status` VARCHAR(50) DEFAULT NULL,
  `creator_id` VARCHAR(100) DEFAULT NULL,
  `creator_name` VARCHAR(100) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 学员记录管理表
CREATE TABLE IF NOT EXISTS `students` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT(20) DEFAULT NULL,
  `student_name` VARCHAR(100) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `birth_date` DATE DEFAULT NULL,
  `department` VARCHAR(100) DEFAULT NULL,
  `position` VARCHAR(100) DEFAULT NULL,
  `employee_id` VARCHAR(50) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  `enrollment_date` DATE DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 学习进度跟踪表
CREATE TABLE IF NOT EXISTS `progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `progress_id` BIGINT(20) DEFAULT NULL,
  `student_id` BIGINT(20) DEFAULT NULL,
  `student_name` VARCHAR(100) DEFAULT NULL,
  `course_id` BIGINT(20) DEFAULT NULL,
  `course_name` VARCHAR(200) DEFAULT NULL,
  `progress_percent` INT(11) DEFAULT 0,
  `completed_lessons` INT(11) DEFAULT 0,
  `total_lessons` INT(11) DEFAULT 0,
  `last_study_time` DATETIME DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 讲师分配表
CREATE TABLE IF NOT EXISTS `instructors` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `instructor_id` BIGINT(20) DEFAULT NULL,
  `instructor_name` VARCHAR(100) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(100) DEFAULT NULL,
  `department` VARCHAR(100) DEFAULT NULL,
  `title` VARCHAR(100) DEFAULT NULL,
  `specialty` VARCHAR(200) DEFAULT NULL,
  `status` VARCHAR(50) DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

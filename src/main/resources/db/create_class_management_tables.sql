-- 班级管理模块数据库表
-- 一个班级可关联N名讲师与N名学员（讲师/学员均来自 students 表）

CREATE TABLE IF NOT EXISTS `teaching_class` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `class_code` VARCHAR(50) DEFAULT NULL COMMENT '班级编码（可选）',
  `class_name` VARCHAR(200) NOT NULL COMMENT '班级名称',
  `description` TEXT DEFAULT NULL COMMENT '班级描述',
  `status` VARCHAR(50) DEFAULT '正常' COMMENT '状态：正常/停用',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_name` (`class_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 班级-讲师关联表（students.user_type=2）
CREATE TABLE IF NOT EXISTS `teaching_class_instructor` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `class_id` BIGINT(20) NOT NULL COMMENT '班级ID teaching_class.id',
  `student_id` BIGINT(20) NOT NULL COMMENT '讲师记录ID students.id',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_instructor` (`class_id`, `student_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级讲师关联表';

-- 班级-学员关联表（students.user_type=1）
CREATE TABLE IF NOT EXISTS `teaching_class_student` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `class_id` BIGINT(20) NOT NULL COMMENT '班级ID teaching_class.id',
  `student_id` BIGINT(20) NOT NULL COMMENT '学员记录ID students.id',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_student` (`class_id`, `student_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级学员关联表';


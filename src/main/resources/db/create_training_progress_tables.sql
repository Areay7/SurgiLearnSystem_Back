-- 护理培训模块：培训资料关联表 + 培训进度表
-- 说明：
-- - training 表作为“培训课程”主表（已存在）
-- - learning_materials 表作为“资料库”（已存在）
-- - 本脚本新增两张表用于关联与进度追踪

USE surgilearn;

-- 培训-资料关联（一个培训包含多份资料，可排序/可必学）
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 培训进度（按用户-培训 维度）
CREATE TABLE IF NOT EXISTS `training_progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL,
  `student_id` BIGINT(20) NOT NULL,
  `student_name` VARCHAR(100) DEFAULT NULL,
  `progress_percent` INT(11) DEFAULT 0,
  `completed_count` INT(11) DEFAULT 0,
  `total_count` INT(11) DEFAULT 0,
  `status` VARCHAR(50) DEFAULT NULL, -- 学习中/已完成
  `last_study_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_student` (`training_id`,`student_id`),
  KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 资料完成明细（按用户-培训-资料 维度，便于视频/文档分别记进度）
CREATE TABLE IF NOT EXISTS `training_material_progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL,
  `material_id` BIGINT(20) NOT NULL,
  `student_id` BIGINT(20) NOT NULL,
  `progress_percent` INT(11) DEFAULT 0,
  `completed` TINYINT(1) DEFAULT 0,
  `last_position` INT(11) DEFAULT 0, -- 视频秒数/阅读页等（前端约定）
  `last_study_time` DATETIME DEFAULT NULL,
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tmp` (`training_id`,`material_id`,`student_id`),
  KEY `idx_training_material` (`training_id`,`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


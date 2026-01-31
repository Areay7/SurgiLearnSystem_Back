-- 培训-班级关联表：指定培训对哪些班级可见（空=全员可见）
CREATE TABLE IF NOT EXISTS `training_class` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL COMMENT '培训ID training.id',
  `class_id` BIGINT(20) NOT NULL COMMENT '班级ID teaching_class.id',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_training_class` (`training_id`, `class_id`),
  KEY `idx_training_id` (`training_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训-班级关联表';

-- 考试-班级关联表：指定考试对哪些班级可见（空=全员可见）
CREATE TABLE IF NOT EXISTS `exam_class` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `exam_id` BIGINT(20) NOT NULL COMMENT '考试ID exam.id',
  `class_id` BIGINT(20) NOT NULL COMMENT '班级ID teaching_class.id',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_class` (`exam_id`, `class_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试-班级关联表';

USE surgilearn;

CREATE TABLE IF NOT EXISTS `user_feedback` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(100) DEFAULT NULL COMMENT '反馈者ID（手机号）',
  `user_name` VARCHAR(100) DEFAULT NULL COMMENT '反馈者姓名',
  `title` VARCHAR(300) DEFAULT NULL COMMENT '反馈标题',
  `content` TEXT COMMENT '反馈内容',
  `rating` INT(11) DEFAULT NULL COMMENT '评分1-5',
  `feedback_type` VARCHAR(50) DEFAULT NULL COMMENT '类型：课程评价/系统建议/问题反馈',
  `relate_id` BIGINT(20) DEFAULT NULL COMMENT '关联ID（培训/课程等）',
  `relate_name` VARCHAR(200) DEFAULT NULL COMMENT '关联名称',
  `status` VARCHAR(30) DEFAULT '待处理' COMMENT '状态：待处理/处理中/已处理',
  `reply_content` TEXT COMMENT '管理员回复',
  `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_feedback_type` (`feedback_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户反馈评价表';

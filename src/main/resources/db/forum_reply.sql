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

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

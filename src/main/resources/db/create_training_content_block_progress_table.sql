-- 培训资料白板内容块进度表（跟踪每个内容块的浏览状态）
CREATE TABLE IF NOT EXISTS `training_content_block_progress` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL COMMENT '培训ID',
  `block_id` BIGINT(20) NOT NULL COMMENT '内容块ID（training_content_blocks.id）',
  `student_id` BIGINT(20) NOT NULL COMMENT '学员ID',
  `block_type` VARCHAR(20) NOT NULL COMMENT '内容块类型：text/image/video/pdf/file',
  `viewed` TINYINT(1) DEFAULT 0 COMMENT '是否已浏览：1-已浏览 0-未浏览',
  `view_duration` INT(11) DEFAULT 0 COMMENT '浏览时长（秒），用于文字/图片',
  `play_progress` INT(11) DEFAULT 0 COMMENT '播放进度（秒/百分比），用于视频',
  `scroll_progress` INT(11) DEFAULT 0 COMMENT '滚动进度（百分比），用于PDF',
  `downloaded` TINYINT(1) DEFAULT 0 COMMENT '是否已下载，用于文件',
  `first_view_time` DATETIME DEFAULT NULL COMMENT '首次浏览时间',
  `last_view_time` DATETIME DEFAULT NULL COMMENT '最后浏览时间',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_block_student` (`block_id`, `student_id`),
  KEY `idx_training_student` (`training_id`, `student_id`),
  KEY `idx_block_id` (`block_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训资料白板内容块进度';

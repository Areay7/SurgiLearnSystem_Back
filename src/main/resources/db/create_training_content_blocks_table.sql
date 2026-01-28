-- 培训资料白板内容块表（从上往下排列：文字、图片、视频、PDF 预览、其他文件下载）
CREATE TABLE IF NOT EXISTS `training_content_blocks` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `training_id` BIGINT(20) NOT NULL COMMENT '培训ID',
  `block_type` VARCHAR(20) NOT NULL COMMENT '类型：text-文字, image-图片, video-视频, pdf-PDF, file-其他文件(下载)',
  `sort_order` INT(11) DEFAULT 0 COMMENT '从上到下排序序号',
  `content` TEXT DEFAULT NULL COMMENT '文字块内容（block_type=text 时使用）',
  `material_id` BIGINT(20) DEFAULT NULL COMMENT '资料ID，关联 learning_materials（图片/视频/pdf/file 时使用）',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_training_id` (`training_id`),
  KEY `idx_sort` (`training_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训资料白板内容块';

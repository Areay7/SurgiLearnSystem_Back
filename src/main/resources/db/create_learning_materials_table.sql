-- 学习资料管理表
CREATE TABLE IF NOT EXISTS `learning_materials` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` VARCHAR(1000) DEFAULT NULL COMMENT '描述',
  `category` VARCHAR(100) DEFAULT NULL COMMENT '分类',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `file_type` VARCHAR(20) DEFAULT NULL COMMENT '文件类型后缀',
  `file_size` BIGINT(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件存储路径',
  `original_name` VARCHAR(255) DEFAULT NULL COMMENT '原始文件名',
  `uploader_id` VARCHAR(100) DEFAULT NULL COMMENT '上传人ID',
  `uploader_name` VARCHAR(100) DEFAULT NULL COMMENT '上传人姓名',
  `view_count` INT(11) DEFAULT 0 COMMENT '浏览次数',
  `download_count` INT(11) DEFAULT 0 COMMENT '下载次数',
  `status` VARCHAR(50) DEFAULT '已发布' COMMENT '状态：已发布/草稿',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学习资料管理表';

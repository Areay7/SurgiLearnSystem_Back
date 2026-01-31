-- 备份模块表（供已有数据库迁移使用）
USE surgilearn;

CREATE TABLE IF NOT EXISTS `backup_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `backup_path` VARCHAR(500) DEFAULT NULL COMMENT '备份文件保存路径',
  `auto_enabled` INT(11) DEFAULT 0 COMMENT '自动备份是否启用 0-否 1-是',
  `schedule_cron` VARCHAR(100) DEFAULT '0 0 2 * * ?' COMMENT 'Cron表达式',
  `schedule_time` VARCHAR(20) DEFAULT '02:00' COMMENT '简易时间 HH:mm',
  `retention_days` INT(11) DEFAULT 30 COMMENT '备份保留天数',
  `include_uploads` INT(11) DEFAULT 1 COMMENT '是否包含uploads 0-否 1-是',
  `include_database` INT(11) DEFAULT 1 COMMENT '是否包含数据库 0-否 1-是',
  `create_time` DATETIME DEFAULT NULL,
  `update_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `backup_record` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(255) DEFAULT NULL,
  `file_path` VARCHAR(500) DEFAULT NULL,
  `file_size` BIGINT(20) DEFAULT 0,
  `backup_type` VARCHAR(20) DEFAULT 'manual',
  `status` VARCHAR(20) DEFAULT 'success',
  `error_msg` TEXT,
  `duration_seconds` INT(11) DEFAULT 0,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO `backup_config` (`id`, `backup_path`, `auto_enabled`, `schedule_cron`, `schedule_time`, `retention_days`, `include_uploads`, `include_database`, `create_time`, `update_time`)
VALUES (1, NULL, 0, '0 0 2 * * ?', '02:00', 30, 1, 1, NOW(), NOW());

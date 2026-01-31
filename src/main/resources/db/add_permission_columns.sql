-- 为已有 user_permission 表添加新列（升级脚本）
-- 若表由旧版 init_database 创建，需执行本脚本。若报错 "Duplicate column name" 说明已存在，可忽略该条。

USE surgilearn;

ALTER TABLE `user_permission` ADD COLUMN `user_phone` VARCHAR(20) DEFAULT NULL COMMENT '用户手机号' AFTER `user_id`;
ALTER TABLE `user_permission` ADD COLUMN `grant_type` VARCHAR(20) DEFAULT 'grant' COMMENT 'grant-授予 revoke-收回' AFTER `is_active`;
ALTER TABLE `user_permission` ADD INDEX `idx_user_phone` (`user_phone`);

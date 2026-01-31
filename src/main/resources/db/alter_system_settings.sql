-- 系统设置表新增配置字段（系统名称、每页显示、Logo、安全设置）
USE surgilearn;

-- 若列已存在会报错，可忽略
ALTER TABLE `system_settings` ADD COLUMN `system_name` VARCHAR(200) DEFAULT '外科护理主管护师培训学习系统' COMMENT '系统名称';
ALTER TABLE `system_settings` ADD COLUMN `page_size` INT DEFAULT 10 COMMENT '每页显示数量';
ALTER TABLE `system_settings` ADD COLUMN `system_logo` VARCHAR(500) DEFAULT NULL COMMENT '系统Logo路径';
ALTER TABLE `system_settings` ADD COLUMN `password_min_length` INT DEFAULT 8 COMMENT '密码最小长度';
ALTER TABLE `system_settings` ADD COLUMN `login_lock_count` INT DEFAULT 5 COMMENT '登录失败锁定次数';

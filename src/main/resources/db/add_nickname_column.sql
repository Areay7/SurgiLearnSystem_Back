-- 为用户表添加昵称字段
USE surgilearn;

ALTER TABLE `login_discussion_forum` 
ADD COLUMN `nickname` VARCHAR(100) DEFAULT NULL COMMENT '昵称' AFTER `password`;

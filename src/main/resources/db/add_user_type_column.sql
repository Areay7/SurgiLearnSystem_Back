-- 为 login_discussion_forum 表增加用户类型字段
-- 0-普通用户 1-管理员

ALTER TABLE `login_discussion_forum`
ADD COLUMN `user_type` INT(11) DEFAULT 0 COMMENT '用户类型 0-普通用户 1-管理员' AFTER `nickname`;


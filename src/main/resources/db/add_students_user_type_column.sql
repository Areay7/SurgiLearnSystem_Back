-- 为 students 表添加 user_type 字段
-- 如果表已存在，执行此脚本添加字段

ALTER TABLE `students`
ADD COLUMN `user_type` INT(11) DEFAULT 1 COMMENT '用户类型 1-学员 2-讲师 3-其他(管理员)' AFTER `employee_id`;

-- 添加索引
ALTER TABLE `students`
ADD KEY `idx_user_type` (`user_type`);

-- 更新现有数据：如果没有指定类型，默认为学员(1)
UPDATE `students` SET `user_type` = 1 WHERE `user_type` IS NULL;

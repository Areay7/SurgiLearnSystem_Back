-- 该脚本用于为已存在的 exam 表添加新字段，
-- 在升级数据库时手动运行或由部署工具执行。

ALTER TABLE `exam`
  ADD COLUMN `teacher` VARCHAR(100) DEFAULT NULL COMMENT '教师',
  ADD COLUMN `attendance` INT(11) DEFAULT 0 COMMENT '出勤人数',
  ADD COLUMN `location` VARCHAR(200) DEFAULT NULL COMMENT '教学地点',
  ADD COLUMN `remark` TEXT COMMENT '备注（特殊情况说明）';

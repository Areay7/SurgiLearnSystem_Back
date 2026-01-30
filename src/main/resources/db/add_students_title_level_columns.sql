-- 为 students 表添加职称和层级字段

ALTER TABLE `students`
ADD COLUMN `title` VARCHAR(50) DEFAULT NULL COMMENT '职称：护士/护师/主管护师/副主任护师/主任护师' AFTER `position`,
ADD COLUMN `level` VARCHAR(10) DEFAULT NULL COMMENT '层级：N0/N1/N2/N3/N4' AFTER `title`;


-- 删除并重建 students 表
-- 警告：此操作会删除所有现有数据！

-- 1. 删除现有表（如果存在）
DROP TABLE IF EXISTS `students`;

-- 2. 重新创建表（包含 user_type 字段）
CREATE TABLE `students` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `student_id` BIGINT(20) DEFAULT NULL COMMENT '学员ID',
  `student_name` VARCHAR(100) DEFAULT NULL COMMENT '学员姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '部门',
  `position` VARCHAR(100) DEFAULT NULL COMMENT '职位',
  `employee_id` VARCHAR(50) DEFAULT NULL COMMENT '员工编号',
  `user_type` INT(11) DEFAULT 1 COMMENT '用户类型 1-学员 2-讲师 3-其他(管理员)',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态',
  `enrollment_date` DATE DEFAULT NULL COMMENT '入学日期',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_department` (`department`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学员记录管理表';

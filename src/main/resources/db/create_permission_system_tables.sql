-- 权限系统扩展表：权限定义、角色、角色权限
USE surgilearn;

-- 权限定义表（系统所有可配置的权限）
CREATE TABLE IF NOT EXISTS `permission_def` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限代码',
  `permission_name` VARCHAR(200) NOT NULL COMMENT '权限名称',
  `module` VARCHAR(80) DEFAULT NULL COMMENT '所属模块',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `sort_order` INT(11) DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限定义表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色代码',
  `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `user_type_flag` INT(11) DEFAULT NULL COMMENT '对应用户类型：1学员 2讲师 3管理员(students表) 或 1管理员(login表)',
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT(20) NOT NULL,
  `permission_code` VARCHAR(100) NOT NULL,
  `create_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_code`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 扩展 user_permission（若已存在列可忽略报错）
-- ALTER TABLE `user_permission` ADD COLUMN `user_phone` VARCHAR(20) DEFAULT NULL COMMENT '用户手机号' AFTER `user_id`;
-- ALTER TABLE `user_permission` ADD COLUMN `grant_type` VARCHAR(20) DEFAULT 'grant' COMMENT 'grant-授予 revoke-收回' AFTER `is_active`;

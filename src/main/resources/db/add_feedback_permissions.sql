-- 补充反馈模块权限（若 init_permission_data 执行较早可能缺失）
USE surgilearn;

INSERT IGNORE INTO `permission_def` (`permission_code`, `permission_name`, `module`, `description`, `sort_order`) VALUES
('feedback:view', '反馈-查看', '交流互动', '查看反馈列表与详情', 93),
('feedback:submit', '反馈-提交', '交流互动', '提交反馈', 94),
('feedback:manage', '反馈-管理', '交流互动', '回复、更新状态、删除反馈', 95);

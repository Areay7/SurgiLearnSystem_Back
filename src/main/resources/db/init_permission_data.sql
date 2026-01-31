-- 初始化权限定义和角色数据
USE surgilearn;

-- 权限定义（覆盖系统各模块）
INSERT IGNORE INTO `permission_def` (`permission_code`, `permission_name`, `module`, `description`, `sort_order`) VALUES
-- 培训
('training:view', '培训-查看', '培训管理', '查看培训列表与详情', 10),
('training:create', '培训-创建', '培训管理', '创建培训', 11),
('training:edit', '培训-编辑', '培训管理', '编辑培训信息', 12),
('training:delete', '培训-删除', '培训管理', '删除培训', 13),
('training:materials', '培训-资料编辑', '培训管理', '编辑培训资料白板', 14),
('training:progress', '培训-进度查看', '培训管理', '查看学员学习进度', 15),
-- 考试
('exam:view', '考试-查看', '考试管理', '查看考试列表与详情', 20),
('exam:create', '考试-创建', '考试管理', '创建考试', 21),
('exam:edit', '考试-编辑', '考试管理', '编辑考试', 22),
('exam:delete', '考试-删除', '考试管理', '删除考试', 23),
('exam:records', '考试-记录查看', '考试管理', '查看考试记录与成绩', 24),
('exam:take', '考试-参加', '考试管理', '参加考试', 25),
-- 题库
('question:view', '题库-查看', '题库管理', '查看题目', 30),
('question:create', '题库-新增', '题库管理', '新增题目', 31),
('question:edit', '题库-编辑', '题库管理', '编辑题目', 32),
('question:delete', '题库-删除', '题库管理', '删除题目', 33),
-- 视频
('video:view', '视频-观看', '视频讲座', '观看视频', 40),
('video:upload', '视频-上传', '视频讲座', '上传视频', 41),
('video:delete', '视频-删除', '视频讲座', '删除视频（仅管理员）', 42),
('video:favorite', '视频-收藏', '视频讲座', '收藏/取消收藏', 43),
-- 资料
('material:view', '资料-查看', '学习资料', '查看学习资料', 50),
('material:create', '资料-新增', '学习资料', '新增学习资料', 51),
('material:edit', '资料-编辑', '学习资料', '编辑学习资料', 52),
('material:delete', '资料-删除', '学习资料', '删除学习资料', 53),
('material:download', '资料-下载', '学习资料', '下载资料', 54),
-- 资源共享
('resource:view', '资源-查看', '资源共享', '查看共享资源', 60),
('resource:upload', '资源-上传', '资源共享', '上传资源', 61),
('resource:delete', '资源-删除', '资源共享', '删除资源', 62),
('resource:download', '资源-下载', '资源共享', '下载资源', 63),
-- 班级
('class:view', '班级-查看', '班级管理', '查看班级', 70),
('class:create', '班级-创建', '班级管理', '创建班级', 71),
('class:edit', '班级-编辑', '班级管理', '编辑班级', 72),
('class:delete', '班级-删除', '班级管理', '删除班级', 73),
('class:students', '班级-学员管理', '班级管理', '管理班级学员', 74),
('class:instructors', '班级-讲师管理', '班级管理', '管理班级讲师', 75),
-- 课程安排
('schedule:view', '课程安排-查看', '课程安排', '查看课程安排', 80),
('schedule:create', '课程安排-新建', '课程安排', '新建课程安排', 81),
('schedule:edit', '课程安排-编辑', '课程安排', '编辑课程安排', 82),
('schedule:delete', '课程安排-删除', '课程安排', '删除课程安排', 83),
-- 论坛
('forum:view', '论坛-查看', '交流互动', '查看论坛帖子', 90),
('forum:post', '论坛-发帖', '交流互动', '发帖与回复', 91),
('forum:manage', '论坛-管理', '交流互动', '管理帖子（置顶/锁定）', 92),
-- 反馈
('feedback:view', '反馈-查看', '交流互动', '查看反馈', 93),
('feedback:submit', '反馈-提交', '交流互动', '提交反馈', 94),
-- 证书
('certificate:view', '证书-查看', '证书管理', '查看我的证书', 100),
('certificate:issue', '证书-颁发', '证书管理', '颁发证书', 101),
('certificate:manage', '证书-管理', '证书管理', '证书模板管理', 102),
-- 用户与系统
('user:view', '用户-查看', '系统管理', '查看用户列表', 110),
('user:edit', '用户-编辑', '系统管理', '编辑用户信息', 111),
('user:resetPwd', '用户-重置密码', '系统管理', '重置用户密码', 112),
('progress:view', '进度-查看', '系统管理', '查看学习进度跟踪', 120),
('system:settings', '系统-设置', '系统管理', '系统设置', 130),
('system:backup', '系统-备份', '系统管理', '数据备份', 131),
('permission:manage', '权限-管理', '系统管理', '权限管理', 132);

-- 角色
INSERT IGNORE INTO `role` (`role_code`, `role_name`, `description`, `user_type_flag`) VALUES
('student', '学员', '普通学员，可学习、参加考试、查看资料', 1),
('instructor', '讲师', '讲师，可创建培训、考试、管理班级', 2),
('admin', '管理员', '系统管理员，拥有全部权限', 3);

-- 角色权限（管理员拥有全部）
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_code`)
SELECT r.id, p.permission_code FROM role r, permission_def p
WHERE r.role_code = 'admin';

-- 讲师权限
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_code`)
SELECT r.id, p.permission_code FROM role r, permission_def p
WHERE r.role_code = 'instructor' AND p.permission_code IN (
'training:view','training:create','training:edit','training:materials','training:progress',
'exam:view','exam:create','exam:edit','exam:records','exam:take',
'question:view','question:create','question:edit','question:delete',
'video:view','video:upload','video:favorite',
'material:view','material:create','material:edit','material:download',
'resource:view','resource:upload','resource:download',
'class:view','class:students','class:instructors',
'schedule:view','schedule:create','schedule:edit','schedule:delete',
'forum:view','forum:post','feedback:view','feedback:submit',
'certificate:view','progress:view');

-- 学员权限
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_code`)
SELECT r.id, p.permission_code FROM role r, permission_def p
WHERE r.role_code = 'student' AND p.permission_code IN (
'training:view','exam:view','exam:take','question:view',
'video:view','video:favorite','material:view','material:download',
'resource:view','resource:download','schedule:view',
'forum:view','forum:post','feedback:view','feedback:submit','certificate:view');

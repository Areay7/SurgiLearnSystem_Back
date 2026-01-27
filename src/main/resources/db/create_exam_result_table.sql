-- 考试结果表
CREATE TABLE IF NOT EXISTS `exam_result` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `exam_id` BIGINT(20) DEFAULT NULL COMMENT '考试ID',
  `exam_name` VARCHAR(200) DEFAULT NULL COMMENT '考试名称',
  `student_id` VARCHAR(100) DEFAULT NULL COMMENT '学员ID（手机号）',
  `student_name` VARCHAR(100) DEFAULT NULL COMMENT '学员姓名',
  `answers` TEXT COMMENT '答案JSON格式：{"questionId1": "A", "questionId2": "A,B"}',
  `total_score` INT(11) DEFAULT 0 COMMENT '总分',
  `obtained_score` INT(11) DEFAULT 0 COMMENT '得分',
  `pass_score` INT(11) DEFAULT 0 COMMENT '及格分',
  `status` VARCHAR(50) DEFAULT NULL COMMENT '状态：进行中、已完成、已阅卷',
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `submit_time` DATETIME DEFAULT NULL COMMENT '提交时间',
  `duration` INT(11) DEFAULT 0 COMMENT '用时（分钟）',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试结果表';

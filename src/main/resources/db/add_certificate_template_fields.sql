-- 给 certificate_issue 增加证书模板相关字段：盖章图片与证书正文
ALTER TABLE `certificate_issue`
  ADD COLUMN `stamp_path` VARCHAR(500) DEFAULT NULL COMMENT '盖章图片存储路径' AFTER `issue_note`,
  ADD COLUMN `content_text` TEXT COMMENT '证书正文（可编辑）' AFTER `stamp_path`;


package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 证书颁发功能实体
 */
@Data
@TableName("certificate_issue")
public class CertificateIssue {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long certificateId;
    private Date issueDate;
    private String certificateType;
    private String holderName;
    private String holderId;
    private String trainingCourse;
    private String organization;
    private Date expiryDate;
    private String certificateStatus;
    private String issueNote;
    /** 盖章图片存储路径（服务端文件路径或可访问路径） */
    private String stampPath;
    /** 证书正文（可编辑） */
    private String contentText;
    private Date createTime;
    private Date updateTime;
}

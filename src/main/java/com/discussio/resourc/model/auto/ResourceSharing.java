package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 资源共享平台实体
 */
@Data
@TableName("resource_sharing")
public class ResourceSharing {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long resourceId;
    private String resourceName;
    private String resourceType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadDate;
    private String uploadUser;
    private Integer downloadCount;
    private String resourceDesc;
    private String filePath;
    private Integer isApproved;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approvalDate;
}

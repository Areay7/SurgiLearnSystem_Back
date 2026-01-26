package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private Date uploadDate;
    private String uploadUser;
    private Integer downloadCount;
    private String resourceDesc;
    private String filePath;
    private Integer isApproved;
    private Date approvalDate;
}

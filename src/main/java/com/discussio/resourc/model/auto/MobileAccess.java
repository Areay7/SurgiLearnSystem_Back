package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 移动访问支持实体
 */
@Data
@TableName("mobile_access")
public class MobileAccess {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long accessId;
    private String userId;
    private String deviceType;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
    private Date accessTime;
    private String ipAddress;
    private String location;
    private String accessStatus;
    private String errorCode;
    private String errorMsg;
}

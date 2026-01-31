package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统设置选项实体
 */
@Data
@TableName("system_settings")
public class SystemSettings {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long settingsId;
    private String systemName;
    private Integer pageSize;
    private String systemLogo;
    private Integer passwordMinLength;
    private Integer loginLockCount;
    private String courseType;
    private String learningMode;
    private String examTimeLimit;
    private String videoQuality;
    private String questionBankType;
    private String updateFrequency;
    private String certificateType;
    private String supportedLanguages;
    private String customerServiceEmail;
    private String systemVersion;
}

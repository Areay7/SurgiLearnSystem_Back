package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 备份配置实体
 */
@Data
@TableName("backup_config")
public class BackupConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String backupPath;
    private Integer autoEnabled;
    private String scheduleCron;
    private String scheduleTime;
    private Integer retentionDays;
    private Integer includeUploads;
    private Integer includeDatabase;
    private Date createTime;
    private Date updateTime;
}

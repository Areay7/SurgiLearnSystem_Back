package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 备份记录实体
 */
@Data
@TableName("backup_record")
public class BackupRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String backupType;
    private String status;
    private String errorMsg;
    private Integer durationSeconds;
    private Date createTime;
}

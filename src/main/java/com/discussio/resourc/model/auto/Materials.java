package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 学习资料管理实体
 */
@Data
@TableName("materials")
public class Materials {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long materialId;
    private String materialName;
    private String materialType;
    private String filePath;
    private String fileSize;
    private String description;
    private String category;
    private String uploadUserId;
    private String uploadUserName;
    private Date uploadTime;
    private Integer downloadCount;
    private Integer viewCount;
    private String status;
    private Date createTime;
    private Date updateTime;
}

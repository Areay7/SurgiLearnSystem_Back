package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 学习资料实体
 */
@Data
@TableName("learning_materials")
public class LearningMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 分类 */
    private String category;

    /** 标签（逗号分隔） */
    private String tags;

    /** 文件类型（后缀） */
    private String fileType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件存储路径 */
    private String filePath;

    /** 原始文件名 */
    private String originalName;

    /** 上传人ID */
    private String uploaderId;

    /** 上传人名称 */
    private String uploaderName;

    /** 浏览次数 */
    private Integer viewCount;

    /** 下载次数 */
    private Integer downloadCount;

    /** 状态：已发布/草稿 */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

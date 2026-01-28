package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 培训资料白板内容块
 * 类型：text-文字, image-图片, video-视频, pdf-PDF, file-其他文件(下载)
 */
@Data
@TableName("training_content_blocks")
public class TrainingContentBlock {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainingId;
    /** 类型：text, image, video, pdf, file */
    private String blockType;
    private Integer sortOrder;
    /** 文字块内容（block_type=text 时使用） */
    private String content;
    /** 资料ID，关联 learning_materials（图片/视频/pdf/file 时使用） */
    private Long materialId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

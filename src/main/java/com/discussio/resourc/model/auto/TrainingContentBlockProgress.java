package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 培训资料白板内容块进度
 */
@Data
@TableName("training_content_block_progress")
public class TrainingContentBlockProgress {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainingId;
    private Long blockId;
    private Long studentId;
    private String blockType;
    
    /** 是否已浏览：1-已浏览 0-未浏览 */
    private Integer viewed;
    /** 浏览时长（秒），用于文字/图片 */
    private Integer viewDuration;
    /** 播放进度（秒/百分比），用于视频 */
    private Integer playProgress;
    /** 滚动进度（百分比），用于PDF */
    private Integer scrollProgress;
    /** 是否已下载，用于文件 */
    private Integer downloaded;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date firstViewTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastViewTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

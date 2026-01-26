package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 视频讲座播放实体
 */
@Data
@TableName("videos")
public class Videos {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long videoId;
    private String videoTitle;
    private String videoUrl;
    private String videoType;
    private String description;
    private String instructorId;
    private String instructorName;
    private Integer duration;
    private String thumbnailUrl;
    private Integer viewCount;
    private Integer likeCount;
    private String status;
    private Date publishTime;
    private Date createTime;
    private Date updateTime;
}

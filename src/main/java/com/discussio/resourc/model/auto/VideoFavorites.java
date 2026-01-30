package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 视频收藏实体
 */
@Data
@TableName("video_favorites")
public class VideoFavorites {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String userId;
    private Long videoId;
    private Date createTime;
}

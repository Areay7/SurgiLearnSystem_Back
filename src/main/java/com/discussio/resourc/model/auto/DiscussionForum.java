package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 讨论论坛模块实体
 */
@Data
@TableName("discussion_forum")
public class DiscussionForum {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long discussionId;
    private String forumTitle;
    private String posterId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date postTime;
    
    private String content;
    private Integer replyCount;
    private Integer likeCount;
    private Integer isSticky;
    private Integer isLocked;
    private String lastReplyId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastReplyTime;
    
    private String categoryId;
}

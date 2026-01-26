package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 学习进度跟踪实体
 */
@Data
@TableName("progress")
public class Progress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long progressId;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private Integer progressPercent;
    private Integer completedLessons;
    private Integer totalLessons;
    private Date lastStudyTime;
    private String status;
    private Date createTime;
    private Date updateTime;
}

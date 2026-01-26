package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 考试系统实体
 */
@Data
@TableName("exam")
public class Exam {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examId;
    private String examName;
    private String examType;
    private Date examDate;
    private String startTime;
    private String endTime;
    private Integer duration;
    private Integer totalScore;
    private Integer passScore;
    private String questionIds;
    private String status;
    private String creatorId;
    private String creatorName;
    private Date createTime;
    private Date updateTime;
}

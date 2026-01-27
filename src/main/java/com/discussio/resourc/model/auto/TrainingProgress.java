package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 培训进度（按用户-培训）
 */
@Data
@TableName("training_progress")
public class TrainingProgress {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainingId;
    private Long studentId;
    private String studentName;

    private Integer progressPercent;
    private Integer completedCount;
    private Integer totalCount;
    private String status; // 学习中/已完成
    private Date lastStudyTime;

    private Date createTime;
    private Date updateTime;
}


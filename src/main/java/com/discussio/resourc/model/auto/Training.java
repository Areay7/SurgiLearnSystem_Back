package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 护理培训实体
 */
@Data
@TableName("training")
public class Training {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long trainingId;
    private String trainingName;
    private String trainingType;
    private String description;
    private Date startDate;
    private Date endDate;
    private String instructorId;
    private String instructorName;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String status;
    private Date createTime;
    private Date updateTime;
}

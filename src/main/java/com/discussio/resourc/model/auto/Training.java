package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.discussio.resourc.common.config.MultiFormatDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    private String instructorId;
    private String instructorName;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String status;
    private Date createTime;
    private Date updateTime;
}

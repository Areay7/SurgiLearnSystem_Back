package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.discussio.resourc.common.config.MultiFormatDateDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.Date;

/**
 * 课程安排设置实体
 */
@Data
@TableName("schedule")
public class Schedule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scheduleId;
    private String courseName;
    private String courseType;

    @JsonDeserialize(using = MultiFormatDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date scheduleDate;
    private String startTime;
    private String endTime;
    private String classroom;
    private String instructorId;
    private String instructorName;
    private Integer maxStudents;
    private Integer enrolledStudents;
    private String status;
    private String description;
    private Date createTime;
    private Date updateTime;
}

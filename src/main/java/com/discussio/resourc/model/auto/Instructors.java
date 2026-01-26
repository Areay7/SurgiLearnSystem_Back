package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 讲师分配实体
 */
@Data
@TableName("instructors")
public class Instructors {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long instructorId;
    private String instructorName;
    private String phone;
    private String email;
    private String department;
    private String title;
    private String specialty;
    private String status;
    private Date createTime;
    private Date updateTime;
}

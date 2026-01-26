package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 学员记录管理实体
 */
@Data
@TableName("students")
public class Students {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String studentName;
    private String phone;
    private String email;
    private String gender;
    private Date birthDate;
    private String department;
    private String position;
    private String employeeId;
    private String status;
    private Date enrollmentDate;
    private Date createTime;
    private Date updateTime;
}

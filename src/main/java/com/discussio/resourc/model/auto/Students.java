package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;
    
    private String department;
    private String position;
    /** 职称：护士/护师/主管护师/副主任护师/主任护师 */
    private String title;
    /** 层级：N0/N1/N2/N3/N4 */
    private String level;
    private String employeeId;
    private Integer userType; // 用户类型 1-学员 2-讲师 3-其他(管理员)
    private String status;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date enrollmentDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

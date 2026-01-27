package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 考试结果实体
 */
@Data
@TableName("exam_result")
public class ExamResult {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long examId; // 考试ID
    private String examName; // 考试名称
    private String studentId; // 学员ID（手机号）
    private String studentName; // 学员姓名
    private String answers; // 答案JSON格式：{"questionId1": "A", "questionId2": "A,B"}
    private Integer totalScore; // 总分
    private Integer obtainedScore; // 得分
    private Integer passScore; // 及格分
    private String status; // 状态：进行中、已完成、已阅卷
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime; // 开始时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime; // 提交时间
    
    private Integer duration; // 用时（分钟）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}

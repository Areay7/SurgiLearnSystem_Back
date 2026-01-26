package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 在线题库实体
 */
@Data
@TableName("question_bank")
public class QuestionBank {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long questionId;
    private String questionType;
    private String questionContent;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private String explanation;
    private String category;
    private String difficulty;
    private Integer score;
    private String creatorId;
    private String creatorName;
    private Date createTime;
    private Date updateTime;
}

package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 培训资料进度（按用户-培训-资料）
 */
@Data
@TableName("training_material_progress")
public class TrainingMaterialProgress {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainingId;
    private Long materialId;
    private Long studentId;

    private Integer progressPercent;
    private Integer completed; // 1完成 0未完成
    private Integer lastPosition; // 秒/页等（前端约定）
    private Date lastStudyTime;

    private Date createTime;
    private Date updateTime;
}


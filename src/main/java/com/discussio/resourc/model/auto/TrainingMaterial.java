package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 培训-资料关联
 */
@Data
@TableName("training_material")
public class TrainingMaterial {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainingId;
    private Long materialId;
    private Integer sortOrder;
    private Integer required; // 1必学 0选学
    private Date createTime;
}


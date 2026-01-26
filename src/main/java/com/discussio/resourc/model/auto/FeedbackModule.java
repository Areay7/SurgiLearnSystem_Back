package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 反馈评价模块实体
 */
@Data
@TableName("feedback_module")
public class FeedbackModule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long moduleId;
    private String moduleName;
    private String moduleType;
    private String parentModuleId;
    private String sortOrder;
    private Integer isActive;
    private Date createTime;
    private Date updateTime;
    private String remark;
    private String iconUrl;
}

package com.discussio.resourc.model.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户权限管理实体
 */
@Data
@TableName("user_permission")
public class UserPermission {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String permissionCode;
    private String permissionName;
    private Integer isActive;
}

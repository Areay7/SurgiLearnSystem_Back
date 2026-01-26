package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.UserPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户权限管理Mapper接口
 */
@Mapper
public interface UserPermissionMapper extends BaseMapper<UserPermission> {
    UserPermission selectUserPermissionById(Long id);
}

package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.SystemSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统设置选项Mapper接口
 */
@Mapper
public interface SystemSettingsMapper extends BaseMapper<SystemSettings> {
    SystemSettings selectSystemSettingsById(Long id);
}

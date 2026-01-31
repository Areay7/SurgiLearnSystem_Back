package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.SystemSettings;

/**
 * 系统设置选项Service接口
 */
public interface ISystemSettingsService extends IService<SystemSettings> {
    SystemSettings getOrCreateConfig();
    int saveConfig(SystemSettings config);
    int updateLogoPath(String relativePath);
    SystemSettings selectSystemSettingsById(Long id);
    int insertSystemSettings(SystemSettings systemSettings);
    int updateSystemSettings(SystemSettings systemSettings);
    int deleteSystemSettingsByIds(String ids);
    int deleteSystemSettingsById(Long id);
}

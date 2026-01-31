package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.SystemSettingsMapper;
import com.discussio.resourc.model.auto.SystemSettings;
import com.discussio.resourc.service.ISystemSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 系统设置选项Service业务处理
 */
@Service
public class SystemSettingsServiceImpl extends ServiceImpl<SystemSettingsMapper, SystemSettings>
        implements ISystemSettingsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsServiceImpl.class);
    private static final Long CONFIG_ID = 1L;

    @Override
    public SystemSettings getOrCreateConfig() {
        SystemSettings cfg = this.getById(CONFIG_ID);
        if (cfg == null) {
            cfg = new SystemSettings();
            cfg.setId(CONFIG_ID);
            cfg.setSystemName("外科护理主管护师培训学习系统");
            cfg.setPageSize(10);
            cfg.setPasswordMinLength(8);
            cfg.setLoginLockCount(5);
            cfg.setCourseType("default");
            this.save(cfg);
        }
        if (cfg.getSystemName() == null) cfg.setSystemName("外科护理主管护师培训学习系统");
        if (cfg.getPageSize() == null) cfg.setPageSize(10);
        if (cfg.getPasswordMinLength() == null) cfg.setPasswordMinLength(8);
        if (cfg.getLoginLockCount() == null) cfg.setLoginLockCount(5);
        return cfg;
    }

    @Override
    public int saveConfig(SystemSettings config) {
        if (config == null) return 0;
        SystemSettings existing = getOrCreateConfig();
        if (config.getSystemName() != null) existing.setSystemName(config.getSystemName());
        if (config.getPageSize() != null) existing.setPageSize(config.getPageSize());
        if (config.getSystemLogo() != null) existing.setSystemLogo(config.getSystemLogo());
        if (config.getPasswordMinLength() != null) existing.setPasswordMinLength(config.getPasswordMinLength());
        if (config.getLoginLockCount() != null) existing.setLoginLockCount(config.getLoginLockCount());
        return this.updateById(existing) ? 1 : 0;
    }

    @Override
    public int updateLogoPath(String relativePath) {
        UpdateWrapper<SystemSettings> uw = new UpdateWrapper<>();
        uw.eq("id", CONFIG_ID);
        uw.set("system_logo", relativePath);
        return this.baseMapper.update(null, uw);
    }

    @Override
    public SystemSettings selectSystemSettingsById(Long id) {
        return this.baseMapper.selectSystemSettingsById(id);
    }

    @Override
    public int insertSystemSettings(SystemSettings systemSettings) {
        return this.baseMapper.insert(systemSettings);
    }

    @Override
    public int updateSystemSettings(SystemSettings systemSettings) {
        return this.baseMapper.updateById(systemSettings);
    }

    @Override
    public int deleteSystemSettingsByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteSystemSettingsById(Long id) {
        if (id == null) {
            throw new RuntimeException("系统设置选项id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }
}

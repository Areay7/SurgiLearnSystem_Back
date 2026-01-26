package com.discussio.resourc.service.impl;

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

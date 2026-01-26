package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.MobileAccessMapper;
import com.discussio.resourc.model.auto.MobileAccess;
import com.discussio.resourc.service.IMobileAccessService;
import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 移动访问支持Service业务处理
 */
@Service
public class MobileAccessServiceImpl extends ServiceImpl<MobileAccessMapper, MobileAccess>
        implements IMobileAccessService {
    
    private static final Logger logger = LoggerFactory.getLogger(MobileAccessServiceImpl.class);

    @Override
    public MobileAccess selectMobileAccessById(Long id) {
        return this.baseMapper.selectMobileAccessById(id);
    }

    @Override
    public List<MobileAccess> selectMobileAccessList(Wrapper<MobileAccess> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<MobileAccess> selectMobileAccessList(MobileAccess mobileAccess) {
        Map<String, Object> map = BeanUtil.beanToMap(mobileAccess, true, true);
        QueryWrapper<MobileAccess> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(map, false);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertMobileAccess(MobileAccess mobileAccess) {
        if (StringUtils.isEmpty(mobileAccess.getUserId())) {
            throw new RuntimeException("移动访问支持.用户id不能为空！");
        }
        if (mobileAccess.getAccessTime() == null) {
            mobileAccess.setAccessTime(new Date());
        }
        
        return this.baseMapper.insert(mobileAccess);
    }

    @Override
    public int updateMobileAccess(MobileAccess mobileAccess) {
        return this.baseMapper.updateById(mobileAccess);
    }

    @Override
    public int deleteMobileAccessByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteMobileAccessById(Long id) {
        if (id == null) {
            throw new RuntimeException("移动访问支持id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int updateMobileAccessVisible(MobileAccess mobileAccess) {
        return this.baseMapper.updateById(mobileAccess);
    }

    @Override
    public List<MobileAccess> getMobileAccessListByDeviceType(String deviceType) {
        if (deviceType == null) {
            throw new RuntimeException("移动访问支持设备类型不能为空");
        }
        return this.baseMapper.selectMobileAccessListByDeviceType(deviceType);
    }

    @Override
    public List<MobileAccess> getMobileAccessListByAccessStatus(String accessStatus) {
        if (accessStatus == null) {
            throw new RuntimeException("移动访问支持访问状态不能为空");
        }
        return this.baseMapper.selectMobileAccessListByAccessStatus(accessStatus);
    }
}

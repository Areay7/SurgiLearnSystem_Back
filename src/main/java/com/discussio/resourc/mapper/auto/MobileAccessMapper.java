package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.MobileAccess;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 移动访问支持Mapper接口
 */
@Mapper
public interface MobileAccessMapper extends BaseMapper<MobileAccess> {
    MobileAccess selectMobileAccessById(Long id);
    List<MobileAccess> selectMobileAccessListByDeviceType(String deviceType);
    List<MobileAccess> selectMobileAccessListByAccessStatus(String accessStatus);
}

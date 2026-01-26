package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.MobileAccess;

import java.util.List;

/**
 * 移动访问支持Service接口
 */
public interface IMobileAccessService extends IService<MobileAccess> {
    MobileAccess selectMobileAccessById(Long id);
    List<MobileAccess> selectMobileAccessList(Wrapper<MobileAccess> queryWrapper);
    List<MobileAccess> selectMobileAccessList(MobileAccess mobileAccess);
    int insertMobileAccess(MobileAccess mobileAccess);
    int updateMobileAccess(MobileAccess mobileAccess);
    int deleteMobileAccessByIds(String ids);
    int deleteMobileAccessById(Long id);
    int updateMobileAccessVisible(MobileAccess mobileAccess);
    List<MobileAccess> getMobileAccessListByDeviceType(String deviceType);
    List<MobileAccess> getMobileAccessListByAccessStatus(String accessStatus);
}

package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.ResourceSharing;

import java.util.List;

/**
 * 资源共享平台Service接口
 */
public interface IResourceSharingService extends IService<ResourceSharing> {
    ResourceSharing selectResourceSharingById(Long id);
    List<ResourceSharing> selectResourceSharingList(Wrapper<ResourceSharing> queryWrapper);
    List<ResourceSharing> selectResourceSharingList(ResourceSharing resourceSharing);
    int insertResourceSharing(ResourceSharing resourceSharing);
    int updateResourceSharing(ResourceSharing resourceSharing);
    int deleteResourceSharingByIds(String ids);
    int deleteResourceSharingById(Long id);
    int updateResourceSharingVisible(ResourceSharing resourceSharing);
    List<ResourceSharing> getResourceSharingListByResourceType(String resourceType);
}

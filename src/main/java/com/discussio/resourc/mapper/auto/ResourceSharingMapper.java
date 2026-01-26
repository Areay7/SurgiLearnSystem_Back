package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.ResourceSharing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 资源共享平台Mapper接口
 */
@Mapper
public interface ResourceSharingMapper extends BaseMapper<ResourceSharing> {
    ResourceSharing selectResourceSharingById(Long id);
    List<ResourceSharing> selectResourceSharingListByResourceType(String resourceType);
}

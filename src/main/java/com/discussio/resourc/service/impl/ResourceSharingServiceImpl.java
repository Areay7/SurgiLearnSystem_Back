package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.ResourceSharingMapper;
import com.discussio.resourc.model.auto.ResourceSharing;
import com.discussio.resourc.service.IResourceSharingService;
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
 * 资源共享平台Service业务处理
 */
@Service
public class ResourceSharingServiceImpl extends ServiceImpl<ResourceSharingMapper, ResourceSharing>
        implements IResourceSharingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ResourceSharingServiceImpl.class);

    @Override
    public ResourceSharing selectResourceSharingById(Long id) {
        return this.baseMapper.selectResourceSharingById(id);
    }

    @Override
    public List<ResourceSharing> selectResourceSharingList(Wrapper<ResourceSharing> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<ResourceSharing> selectResourceSharingList(ResourceSharing resourceSharing) {
        Map<String, Object> map = BeanUtil.beanToMap(resourceSharing, true, true);
        QueryWrapper<ResourceSharing> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(map, false);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertResourceSharing(ResourceSharing resourceSharing) {
        if (StringUtils.isEmpty(resourceSharing.getResourceName())) {
            throw new RuntimeException("资源共享平台.资源名称不能为空！");
        } else if (resourceSharing.getResourceName().length() > 160) {
            throw new RuntimeException("资源共享平台.资源名称长度不能超过160个字符");
        }
        
        if (resourceSharing.getUploadDate() == null) {
            resourceSharing.setUploadDate(new Date());
        }
        
        return this.baseMapper.insert(resourceSharing);
    }

    @Override
    public int updateResourceSharing(ResourceSharing resourceSharing) {
        if (StringUtils.isEmpty(resourceSharing.getResourceName())) {
            throw new RuntimeException("资源共享平台资源名称不能为空！");
        }
        return this.baseMapper.updateById(resourceSharing);
    }

    @Override
    public int deleteResourceSharingByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteResourceSharingById(Long id) {
        if (id == null) {
            throw new RuntimeException("资源共享平台id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int updateResourceSharingVisible(ResourceSharing resourceSharing) {
        return this.baseMapper.updateById(resourceSharing);
    }

    @Override
    public List<ResourceSharing> getResourceSharingListByResourceType(String resourceType) {
        if (resourceType == null) {
            throw new RuntimeException("资源共享平台资源类型不能为空");
        }
        return this.baseMapper.selectResourceSharingListByResourceType(resourceType);
    }
}

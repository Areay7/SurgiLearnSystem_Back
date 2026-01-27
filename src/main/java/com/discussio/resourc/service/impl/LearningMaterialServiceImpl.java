package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.LearningMaterialMapper;
import com.discussio.resourc.model.auto.LearningMaterial;
import com.discussio.resourc.service.ILearningMaterialService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class LearningMaterialServiceImpl extends ServiceImpl<LearningMaterialMapper, LearningMaterial>
        implements ILearningMaterialService {

    private static final Logger logger = LoggerFactory.getLogger(LearningMaterialServiceImpl.class);

    @Override
    public LearningMaterial selectLearningMaterialById(Long id) {
        return this.baseMapper.selectLearningMaterialById(id);
    }

    @Override
    public List<LearningMaterial> selectLearningMaterialList(Wrapper<LearningMaterial> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertLearningMaterial(LearningMaterial material) {
        validate(material, true);
        material.setCreateTime(material.getCreateTime() == null ? new Date() : material.getCreateTime());
        material.setUpdateTime(new Date());
        if (material.getViewCount() == null) material.setViewCount(0);
        if (material.getDownloadCount() == null) material.setDownloadCount(0);
        return this.baseMapper.insert(material);
    }

    @Override
    public int updateLearningMaterial(LearningMaterial material) {
        if (material.getId() == null) {
            throw new RuntimeException("资料ID不能为空！");
        }
        validate(material, false);
        material.setUpdateTime(new Date());
        return this.baseMapper.updateById(material);
    }

    @Override
    public int deleteLearningMaterialByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteLearningMaterialById(Long id) {
        if (id == null) throw new RuntimeException("资料ID不能为空");
        return this.baseMapper.deleteById(id);
    }

    private void validate(LearningMaterial m, boolean isCreate) {
        if (StringUtils.isBlank(m.getTitle())) {
            throw new RuntimeException("标题不能为空！");
        }
        if (isCreate && StringUtils.isBlank(m.getFilePath())) {
            throw new RuntimeException("文件路径不能为空，请先上传文件！");
        }
        if (m.getStatus() == null) {
            m.setStatus("已发布");
        }
        if (m.getFileSize() != null && m.getFileSize() < 0) {
            throw new RuntimeException("文件大小非法");
        }
    }
}

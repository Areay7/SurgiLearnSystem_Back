package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.CertificateIssueMapper;
import com.discussio.resourc.model.auto.CertificateIssue;
import com.discussio.resourc.service.ICertificateIssueService;
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
 * 证书颁发功能Service业务处理
 */
@Service
public class CertificateIssueServiceImpl extends ServiceImpl<CertificateIssueMapper, CertificateIssue>
        implements ICertificateIssueService {
    
    private static final Logger logger = LoggerFactory.getLogger(CertificateIssueServiceImpl.class);

    @Override
    public CertificateIssue selectCertificateIssueById(Long id) {
        return this.baseMapper.selectCertificateIssueById(id);
    }

    @Override
    public List<CertificateIssue> selectCertificateIssueList(Wrapper<CertificateIssue> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<CertificateIssue> selectCertificateIssueList(CertificateIssue certificateIssue) {
        Map<String, Object> map = BeanUtil.beanToMap(certificateIssue, true, true);
        QueryWrapper<CertificateIssue> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(map, false);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertCertificateIssue(CertificateIssue certificateIssue) {
        if (certificateIssue.getIssueDate() == null) {
            certificateIssue.setIssueDate(new Date());
        }
        if (StringUtils.isEmpty(certificateIssue.getCertificateType())) {
            throw new RuntimeException("证书颁发功能.证书类型不能为空！");
        }
        if (StringUtils.isEmpty(certificateIssue.getHolderName())) {
            throw new RuntimeException("证书颁发功能.持证人姓名不能为空！");
        }
        if (certificateIssue.getCreateTime() == null) {
            certificateIssue.setCreateTime(new Date());
        }
        if (certificateIssue.getUpdateTime() == null) {
            certificateIssue.setUpdateTime(new Date());
        }
        
        return this.baseMapper.insert(certificateIssue);
    }

    @Override
    public int updateCertificateIssue(CertificateIssue certificateIssue) {
        certificateIssue.setUpdateTime(new Date());
        return this.baseMapper.updateById(certificateIssue);
    }

    @Override
    public int deleteCertificateIssueByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteCertificateIssueById(Long id) {
        if (id == null) {
            throw new RuntimeException("证书颁发功能id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int updateCertificateIssueVisible(CertificateIssue certificateIssue) {
        return this.baseMapper.updateById(certificateIssue);
    }

    @Override
    public List<CertificateIssue> getCertificateIssueListByCertificateType(String certificateType) {
        if (certificateType == null) {
            throw new RuntimeException("证书颁发功能证书类型不能为空");
        }
        return this.baseMapper.selectCertificateIssueListByCertificateType(certificateType);
    }

    @Override
    public List<CertificateIssue> getCertificateIssueListByCertificateStatus(String certificateStatus) {
        if (certificateStatus == null) {
            throw new RuntimeException("证书颁发功能证书状态不能为空");
        }
        return this.baseMapper.selectCertificateIssueListByCertificateStatus(certificateStatus);
    }
}

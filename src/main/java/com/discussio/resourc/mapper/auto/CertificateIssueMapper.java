package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.CertificateIssue;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 证书颁发功能Mapper接口
 */
@Mapper
public interface CertificateIssueMapper extends BaseMapper<CertificateIssue> {
    CertificateIssue selectCertificateIssueById(Long id);
    List<CertificateIssue> selectCertificateIssueListByCertificateType(String certificateType);
    List<CertificateIssue> selectCertificateIssueListByCertificateStatus(String certificateStatus);
}

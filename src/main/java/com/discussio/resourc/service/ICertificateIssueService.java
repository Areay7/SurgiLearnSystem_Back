package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.CertificateIssue;

import java.util.List;

/**
 * 证书颁发功能Service接口
 */
public interface ICertificateIssueService extends IService<CertificateIssue> {
    CertificateIssue selectCertificateIssueById(Long id);
    List<CertificateIssue> selectCertificateIssueList(Wrapper<CertificateIssue> queryWrapper);
    List<CertificateIssue> selectCertificateIssueList(CertificateIssue certificateIssue);
    int insertCertificateIssue(CertificateIssue certificateIssue);
    int updateCertificateIssue(CertificateIssue certificateIssue);
    int deleteCertificateIssueByIds(String ids);
    int deleteCertificateIssueById(Long id);
    int updateCertificateIssueVisible(CertificateIssue certificateIssue);
    List<CertificateIssue> getCertificateIssueListByCertificateType(String certificateType);
    List<CertificateIssue> getCertificateIssueListByCertificateStatus(String certificateStatus);
}

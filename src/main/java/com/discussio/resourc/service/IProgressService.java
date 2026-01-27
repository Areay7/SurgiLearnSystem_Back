package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Progress;

import java.util.List;

/**
 * 学习进度 Service 接口
 */
public interface IProgressService extends IService<Progress> {

    Progress selectProgressById(Long id);

    List<Progress> selectProgressList(Wrapper<Progress> queryWrapper);

    int insertProgress(Progress progress);

    int updateProgress(Progress progress);

    int deleteProgressByIds(String ids);

    int deleteProgressById(Long id);
}


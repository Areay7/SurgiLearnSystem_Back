package com.discussio.resourc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Videos;

import java.util.List;

/**
 * 视频讲座播放Service接口
 */
public interface IVideosService extends IService<Videos> {
    Videos selectVideosById(Long id);
    List<Videos> selectVideosList(Wrapper<Videos> queryWrapper);
    int insertVideos(Videos videos);
    int updateVideos(Videos videos);
    int deleteVideosByIds(String ids);
    int deleteVideosById(Long id);
}

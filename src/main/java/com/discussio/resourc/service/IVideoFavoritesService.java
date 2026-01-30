package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.VideoFavorites;

import java.util.List;

/**
 * 视频收藏Service接口
 */
public interface IVideoFavoritesService extends IService<VideoFavorites> {
    boolean addFavorite(String userId, Long videoId);
    boolean removeFavorite(String userId, Long videoId);
    boolean isFavorited(String userId, Long videoId);
    List<Long> getFavoriteVideoIds(String userId);
}

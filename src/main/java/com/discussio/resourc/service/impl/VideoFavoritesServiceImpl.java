package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.VideoFavoritesMapper;
import com.discussio.resourc.model.auto.VideoFavorites;
import com.discussio.resourc.service.IVideoFavoritesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 视频收藏Service业务处理
 */
@Service
public class VideoFavoritesServiceImpl extends ServiceImpl<VideoFavoritesMapper, VideoFavorites>
        implements IVideoFavoritesService {
    
    private static final Logger logger = LoggerFactory.getLogger(VideoFavoritesServiceImpl.class);

    @Override
    public boolean addFavorite(String userId, Long videoId) {
        if (userId == null || videoId == null) {
            return false;
        }
        
        // 检查是否已收藏
        QueryWrapper<VideoFavorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("video_id", videoId);
        VideoFavorites existing = this.baseMapper.selectOne(queryWrapper);
        
        if (existing != null) {
            return true; // 已收藏，返回成功
        }
        
        // 添加收藏
        VideoFavorites favorite = new VideoFavorites();
        favorite.setUserId(userId);
        favorite.setVideoId(videoId);
        favorite.setCreateTime(new Date());
        
        return this.baseMapper.insert(favorite) > 0;
    }

    @Override
    public boolean removeFavorite(String userId, Long videoId) {
        if (userId == null || videoId == null) {
            return false;
        }
        
        QueryWrapper<VideoFavorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("video_id", videoId);
        
        return this.baseMapper.delete(queryWrapper) > 0;
    }

    @Override
    public boolean isFavorited(String userId, Long videoId) {
        if (userId == null || videoId == null) {
            return false;
        }
        
        QueryWrapper<VideoFavorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("video_id", videoId);
        
        return this.baseMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<Long> getFavoriteVideoIds(String userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        
        QueryWrapper<VideoFavorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        
        List<VideoFavorites> favorites = this.baseMapper.selectList(queryWrapper);
        return favorites.stream()
                .map(VideoFavorites::getVideoId)
                .collect(Collectors.toList());
    }
}

package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.VideosMapper;
import com.discussio.resourc.model.auto.Videos;
import com.discussio.resourc.service.IVideosService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 视频讲座播放Service业务处理
 */
@Service
public class VideosServiceImpl extends ServiceImpl<VideosMapper, Videos>
        implements IVideosService {
    
    private static final Logger logger = LoggerFactory.getLogger(VideosServiceImpl.class);

    @Override
    public Videos selectVideosById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public List<Videos> selectVideosList(Wrapper<Videos> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertVideos(Videos videos) {
        if (StringUtils.isEmpty(videos.getVideoTitle())) {
            throw new RuntimeException("视频标题不能为空！");
        }
        if (videos.getCreateTime() == null) {
            videos.setCreateTime(new Date());
        }
        if (videos.getUpdateTime() == null) {
            videos.setUpdateTime(new Date());
        }
        if (videos.getViewCount() == null) {
            videos.setViewCount(0);
        }
        if (videos.getLikeCount() == null) {
            videos.setLikeCount(0);
        }
        return this.baseMapper.insert(videos);
    }

    @Override
    public int updateVideos(Videos videos) {
        if (StringUtils.isEmpty(videos.getVideoTitle())) {
            throw new RuntimeException("视频标题不能为空！");
        }
        videos.setUpdateTime(new Date());
        return this.baseMapper.updateById(videos);
    }

    @Override
    public int deleteVideosByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteVideosById(Long id) {
        if (id == null) {
            throw new RuntimeException("视频id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }
}

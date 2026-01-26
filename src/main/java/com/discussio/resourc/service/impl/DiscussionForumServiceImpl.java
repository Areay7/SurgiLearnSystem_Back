package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.common.support.ConvertUtil;
import com.discussio.resourc.mapper.auto.DiscussionForumMapper;
import com.discussio.resourc.model.auto.DiscussionForum;
import com.discussio.resourc.service.IDiscussionForumService;
import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 讨论论坛模块Service业务处理
 */
@Service
public class DiscussionForumServiceImpl extends ServiceImpl<DiscussionForumMapper, DiscussionForum>
        implements IDiscussionForumService {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscussionForumServiceImpl.class);

    @Override
    public DiscussionForum selectDiscussionForumById(Long id) {
        return this.baseMapper.selectDiscussionForumById(id);
    }

    @Override
    public List<DiscussionForum> selectDiscussionForumList(Wrapper<DiscussionForum> queryWrapper) {
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<DiscussionForum> selectDiscussionForumList(DiscussionForum discussionForum) {
        Map<String, Object> map = BeanUtil.beanToMap(discussionForum, true, true);
        QueryWrapper<DiscussionForum> queryWrapper = new QueryWrapper<>();
        queryWrapper.allEq(map, false);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public int insertDiscussionForum(DiscussionForum discussionForum) {
        // 验证论坛标题
        if (StringUtils.isEmpty(discussionForum.getForumTitle())) {
            throw new RuntimeException("讨论论坛模块.论坛标题不能为空！");
        } else if (discussionForum.getForumTitle().length() > 300) {
            throw new RuntimeException("讨论论坛模块.论坛标题长度不能超过300个字符");
        }
        
        // 验证发布时间
        if (discussionForum.getPostTime() == null) {
            discussionForum.setPostTime(new java.util.Date());
        }
        
        return this.baseMapper.insert(discussionForum);
    }

    @Override
    public int updateDiscussionForum(DiscussionForum discussionForum) {
        // 验证论坛标题
        if (StringUtils.isEmpty(discussionForum.getForumTitle())) {
            throw new RuntimeException("讨论论坛模块论坛标题不能为空！");
        } else if (discussionForum.getForumTitle().length() > 300) {
            throw new RuntimeException("讨论论坛模块.论坛标题长度不能超过300个字符");
        }
        
        return this.baseMapper.updateById(discussionForum);
    }

    @Override
    public int deleteDiscussionForumByIds(String ids) {
        return this.baseMapper.deleteBatchIds(Arrays.asList(ConvertUtil.toStrArray(ids)));
    }

    @Override
    public int deleteDiscussionForumById(Long id) {
        if (id == null) {
            throw new RuntimeException("讨论论坛模块id不能为空");
        }
        return this.baseMapper.deleteById(id);
    }

    @Override
    public int updateDiscussionForumVisible(DiscussionForum discussionForum) {
        return this.baseMapper.updateById(discussionForum);
    }
}

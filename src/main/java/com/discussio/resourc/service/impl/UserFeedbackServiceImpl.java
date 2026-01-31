package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.UserFeedbackMapper;
import com.discussio.resourc.model.auto.UserFeedback;
import com.discussio.resourc.service.IUserFeedbackService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserFeedbackServiceImpl extends ServiceImpl<UserFeedbackMapper, UserFeedback>
        implements IUserFeedbackService {

    @Override
    public boolean save(UserFeedback entity) {
        if (entity.getCreateTime() == null) entity.setCreateTime(new Date());
        if (entity.getUpdateTime() == null) entity.setUpdateTime(new Date());
        if (entity.getStatus() == null) entity.setStatus("待处理");
        return super.save(entity);
    }

    @Override
    public boolean updateById(UserFeedback entity) {
        entity.setUpdateTime(new Date());
        return super.updateById(entity);
    }
}

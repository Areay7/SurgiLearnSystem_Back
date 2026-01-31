package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.PermissionDefMapper;
import com.discussio.resourc.model.auto.PermissionDef;
import com.discussio.resourc.service.IPermissionDefService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionDefServiceImpl extends ServiceImpl<PermissionDefMapper, PermissionDef> implements IPermissionDefService {
    @Override
    public List<PermissionDef> listAllOrderByModule() {
        return this.list(new QueryWrapper<PermissionDef>().orderByAsc("sort_order", "module", "permission_code"));
    }
}

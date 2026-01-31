package com.discussio.resourc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.discussio.resourc.mapper.auto.RoleMapper;
import com.discussio.resourc.model.auto.Role;
import com.discussio.resourc.service.IRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
    @Override
    public List<Role> listAll() {
        return this.list();
    }
}

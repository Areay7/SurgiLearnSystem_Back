package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.Role;

import java.util.List;

public interface IRoleService extends IService<Role> {
    List<Role> listAll();
}

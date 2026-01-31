package com.discussio.resourc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.discussio.resourc.model.auto.PermissionDef;

import java.util.List;

public interface IPermissionDefService extends IService<PermissionDef> {
    List<PermissionDef> listAllOrderByModule();
}

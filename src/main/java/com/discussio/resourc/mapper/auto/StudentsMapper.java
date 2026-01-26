package com.discussio.resourc.mapper.auto;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.discussio.resourc.model.auto.Students;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学员记录管理Mapper接口
 */
@Mapper
public interface StudentsMapper extends BaseMapper<Students> {
    Students selectStudentsById(Long id);
}

package com.discussio.resourc.model.custom;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class Tablepar {
    private Integer page;
    private Integer limit;
    private String searchText;
}

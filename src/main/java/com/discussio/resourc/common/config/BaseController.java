package com.discussio.resourc.common.config;

import com.discussio.resourc.common.domain.AjaxResult;
import com.discussio.resourc.common.domain.ResultTable;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 基础Controller
 */
public class BaseController {
    
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    protected ResultTable pageTable(List<?> list, long total) {
        return ResultTable.success(list, total);
    }
}

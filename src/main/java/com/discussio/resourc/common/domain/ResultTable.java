package com.discussio.resourc.common.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 表格返回结果
 */
public class ResultTable implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int code;
    private String msg;
    private long count;
    private List<?> data;

    public ResultTable() {
    }

    public ResultTable(int code, String msg, long count, List<?> data) {
        this.code = code;
        this.msg = msg;
        this.count = count;
        this.data = data;
    }

    public static ResultTable success(List<?> data, long count) {
        return new ResultTable(0, "操作成功", count, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}

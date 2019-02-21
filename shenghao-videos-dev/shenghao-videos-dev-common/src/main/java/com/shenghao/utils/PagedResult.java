package com.shenghao.utils;

import java.util.List;

/**
 * 封装分页后的数据格式
 */
public class PagedResult {
    private int page;   //当前页数
    private int total;  //总页数
    private long reconds; //总记录数
    private List<?> rows;  //每行显示的内容

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public long getReconds() {
        return reconds;
    }

    public void setRecords(long reconds) {
        this.reconds = reconds;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }
}

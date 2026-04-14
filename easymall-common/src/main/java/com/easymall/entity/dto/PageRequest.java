package com.easymall.entity.dto;

import lombok.Data;

@Data
public class PageRequest {
    /**
     * 当前页码（从1开始）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 计算偏移量（MyBatis用）
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
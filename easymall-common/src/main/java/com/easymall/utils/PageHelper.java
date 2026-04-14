// easymall-common/src/main/java/com/easymall/utils/PageHelper.java
package com.easymall.utils;

import com.easymall.entity.dto.PageRequest;
import com.easymall.entity.result.PageResult;
import java.util.List;
import java.util.function.Function;

/**
 * 分页工具类 - 提供统一的分页处理逻辑
 */
public class PageHelper {

    /**
     * 手动分页（适用于内存分页）
     */
    public static <T> PageResult<T> manualPage(List<T> list, PageRequest pageRequest) {
        if (list == null || list.isEmpty()) {
            return new PageResult<>(List.of(), 0L,
                    pageRequest.getPageNum(), pageRequest.getPageSize());
        }

        int start = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();
        int end = Math.min(start + pageRequest.getPageSize(), list.size());

        List<T> records = list.subList(Math.min(start, list.size()), end);
        return new PageResult<>(records, (long) list.size(),
                pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    /**
     * 计算偏移量
     */
    public static int calculateOffset(Integer pageNum, Integer pageSize) {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 计算总页数
     */
    public static long calculateTotalPages(long total, int pageSize) {
        return (total + pageSize - 1) / pageSize;
    }
}
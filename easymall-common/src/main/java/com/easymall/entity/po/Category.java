package com.easymall.entity.po;

import lombok.Data;

import java.util.List;

@Data
public class Category {
    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID（顶级分类为0或空字符串）
     */
    private String pCategoryId;

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 子分类列表（用于树形结构）
     */
    private List<Category> children;
}

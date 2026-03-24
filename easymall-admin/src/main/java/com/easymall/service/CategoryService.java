package com.easymall.service;

import com.easymall.entity.dto.CategoryDTO;
import com.easymall.entity.po.Category;

import java.util.List;

public interface CategoryService {

    // ==================== 查询 ====================

    /**
     * 获取分类树（树形结构）
     */
    List<Category> getTreeList();

    /**
     * 获取分类列表（平铺）
     */
    List<Category> getList();

    /**
     * 根据ID获取分类
     */
    Category getById(String categoryId);

    /**
     * 获取分类路径
     */
    CategoryDTO.PathResponse getCategoryPath(String categoryId);

    // ==================== 增删改 ====================

    /**
     * 新增分类
     */
    boolean add(Category category);

    /**
     * 更新分类
     */
    boolean update(Category category);

    /**
     * 删除分类
     */
    boolean delete(String categoryId);

    /**
     * 批量删除
     */
    void batchDelete(List<String> categoryIds);

    // ==================== 排序 ====================

    /**
     * 批量更新排序
     */
    void batchUpdateSort(List<CategoryDTO.Sort> sortList);

    /**
     * 移动分类
     */
    void moveCategory(CategoryDTO.Move moveDTO);
}
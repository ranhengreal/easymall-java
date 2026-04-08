package com.easymall.service;

import com.easymall.entity.dto.BrandDTO;
import com.easymall.entity.po.Brand;

import java.util.List;

public interface BrandService {

    // ==================== 查询 ====================

    /**
     * 获取所有品牌列表
     */
    List<Brand> getList();

    /**
     * 获取启用的品牌列表
     */
    List<Brand> getEnabledList();

    /**
     * 根据ID获取品牌
     */
    Brand getById(String brandId);

    // ==================== 增删改 ====================

    /**
     * 新增品牌
     */
    boolean add(Brand brand);

    /**
     * 更新品牌（包含状态更新）
     */
    boolean update(Brand brand);

    /**
     * 删除品牌
     */
    boolean delete(String brandId);

    /**
     * 批量删除
     */
    void batchDelete(List<String> brandIds);

    // ==================== 排序 ====================

    /**
     * 批量更新排序
     */
    void batchUpdateSort(List<BrandDTO.Sort> sortList);
}
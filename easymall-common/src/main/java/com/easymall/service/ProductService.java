package com.easymall.service;

import com.easymall.entity.dto.ProductDTO;
import com.easymall.entity.po.Product;

import java.util.List;

public interface ProductService {

    // ==================== 查询 ====================

    /**
     * 获取所有商品列表
     */
    List<Product> getList();

    /**
     * 根据ID获取商品（包含SKU信息）
     */
    Product getById(String productId);

    // ==================== 增删改 ====================

    /**
     * 新增商品（包含SKU）
     */
    boolean add(Product product);

    /**
     * 更新商品（包含SKU和状态）
     */
    boolean update(Product product);

    /**
     * 删除商品（同时删除关联的SKU）
     */
    boolean delete(String productId);
}
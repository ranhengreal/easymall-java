package com.easymall.service;

import com.easymall.entity.po.Cart;

import java.util.List;

public interface CartService {

    /**
     * 获取用户购物车列表
     */
    List<Cart> getList(String userId);

    /**
     * 获取用户选中的购物车商品
     */
    List<Cart> getSelectedList(String userId);

    /**
     * 添加商品到购物车
     */
    boolean add(Cart cart, String userId);

    /**
     * 更新购物车商品数量
     */
    boolean updateQuantity(String cartId, Integer quantity);

    /**
     * 更新购物车选中状态
     */
    boolean updateSelected(String cartId, Integer selected);

    /**
     * 批量更新选中状态
     */
    void batchUpdateSelected(List<String> cartIds, Integer selected);

    /**
     * 删除购物车商品
     */
    boolean delete(String cartId);

    /**
     * 批量删除购物车商品
     */
    void batchDelete(List<String> cartIds);

    /**
     * 清空购物车
     */
    void clear(String userId);
}
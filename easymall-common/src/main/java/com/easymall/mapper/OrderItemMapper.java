package com.easymall.mapper;

import com.easymall.entity.po.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    /**
     * 根据订单ID查询订单商品列表
     */
    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") String orderId);

    /**
     * 批量新增订单商品
     */
    @Insert("<script>" +
            "INSERT INTO order_item (item_id, order_id, product_id, product_name, product_image, " +
            "sku_id, spec_values, price, quantity, total_amount) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.itemId}, #{item.orderId}, #{item.productId}, #{item.productName}, #{item.productImage}, " +
            "#{item.skuId}, #{item.specValues}, #{item.price}, #{item.quantity}, #{item.totalAmount})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<OrderItem> items);

    /**
     * 删除订单的所有商品
     */
    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") String orderId);

    /**
     * 获取最大明细ID
     */
    @Select("SELECT MAX(item_id) FROM order_item")
    String getMaxItemId();
}
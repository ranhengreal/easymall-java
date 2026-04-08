package com.easymall.mapper;

import com.easymall.entity.po.ProductSku;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductSkuMapper {

    /**
     * 根据商品ID查询SKU列表
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId}")
    List<ProductSku> selectByProductId(@Param("productId") String productId);

    /**
     * 批量新增SKU
     */
    @Insert("<script>" +
            "INSERT INTO product_sku (sku_id, product_id, spec_values, price, stock, image) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.skuId}, #{item.productId}, #{item.specValues}, #{item.price}, #{item.stock}, #{item.image})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<ProductSku> skuList);

    /**
     * 批量更新SKU
     */
    @Update("<script>" +
            "<foreach collection='list' item='item' separator=';'>" +
            "UPDATE product_sku SET spec_values = #{item.specValues}, price = #{item.price}, " +
            "stock = #{item.stock}, image = #{item.image} WHERE sku_id = #{item.skuId}" +
            "</foreach>" +
            "</script>")
    int batchUpdate(@Param("list") List<ProductSku> skuList);

    /**
     * 删除商品的所有SKU
     */
    @Delete("DELETE FROM product_sku WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") String productId);

    /**
     * 获取最大SKU ID
     */
    @Select("SELECT MAX(sku_id) FROM product_sku")
    String getMaxSkuId();
}
package com.easymall.mapper;

import com.easymall.entity.po.Cart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CartMapper {

    /**
     * 查询用户的购物车列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Cart> selectByUserId(@Param("userId") String userId);

    /**
     * 查询用户选中的购物车商品
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND selected = 1")
    List<Cart> selectSelectedByUserId(@Param("userId") String userId);

    /**
     * 查询用户购物车中是否存在某商品
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId} " +
            "AND (sku_id = #{skuId} OR (sku_id IS NULL AND #{skuId} IS NULL))")
    Cart selectByProduct(@Param("userId") String userId,
                         @Param("productId") String productId,
                         @Param("skuId") String skuId);

    /**
     * 新增购物车商品
     */
    @Insert("INSERT INTO cart (cart_id, user_id, product_id, product_name, product_image, " +
            "sku_id, spec_values, price, quantity, selected) " +
            "VALUES (#{cartId}, #{userId}, #{productId}, #{productName}, #{productImage}, " +
            "#{skuId}, #{specValues}, #{price}, #{quantity}, #{selected})")
    int insert(Cart cart);

    /**
     * 更新购物车商品数量
     */
    @Update("UPDATE cart SET quantity = #{quantity} WHERE cart_id = #{cartId}")
    int updateQuantity(@Param("cartId") String cartId, @Param("quantity") Integer quantity);

    /**
     * 更新购物车选中状态
     */
    @Update("UPDATE cart SET selected = #{selected} WHERE cart_id = #{cartId}")
    int updateSelected(@Param("cartId") String cartId, @Param("selected") Integer selected);

    /**
     * 批量更新选中状态
     */
    @Update("<script>" +
            "UPDATE cart SET selected = #{selected} WHERE cart_id IN " +
            "<foreach collection='cartIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateSelected(@Param("cartIds") List<String> cartIds,
                            @Param("selected") Integer selected);

    /**
     * 删除购物车商品
     */
    @Delete("DELETE FROM cart WHERE cart_id = #{cartId}")
    int deleteById(@Param("cartId") String cartId);

    /**
     * 批量删除购物车商品
     */
    @Delete("<script>" +
            "DELETE FROM cart WHERE cart_id IN " +
            "<foreach collection='cartIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDelete(@Param("cartIds") List<String> cartIds);

    /**
     * 清空用户购物车
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int clearByUserId(@Param("userId") String userId);

    /**
     * 获取最大购物车ID
     */
    @Select("SELECT MAX(cart_id) FROM cart")
    String getMaxCartId();
}
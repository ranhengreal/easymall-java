package com.easymall.mapper;

import com.easymall.entity.po.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductMapper {

    // ==================== 查询 ====================

    /**
     * 查询所有商品
     */
    @Select("SELECT p.*, c.category_name as categoryName, b.brand_name as brandName " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "LEFT JOIN brand b ON p.brand_id = b.brand_id " +
            "ORDER BY p.sort ASC")
    List<Product> selectAll();

    /**
     * 根据ID查询商品
     */
    @Select("SELECT p.*, c.category_name as categoryName, b.brand_name as brandName " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "LEFT JOIN brand b ON p.brand_id = b.brand_id " +
            "WHERE p.product_id = #{productId}")
    Product selectById(@Param("productId") String productId);

    /**
     * 获取最大商品ID
     */
    @Select("SELECT MAX(product_id) FROM product")
    String getMaxProductId();

    /**
     * 检查商品名称是否重复
     */
    @Select("SELECT COUNT(*) FROM product WHERE product_name = #{name}")
    int countByName(@Param("name") String name);

    // ==================== 增删改 ====================

    /**
     * 新增商品
     */
    @Insert("INSERT INTO product (product_id, product_name, category_id, brand_id, " +
            "main_image, images, description, price, stock, sort, status) " +
            "VALUES (#{productId}, #{productName}, #{categoryId}, #{brandId}, " +
            "#{mainImage}, #{images}, #{description}, #{price}, #{stock}, #{sort}, #{status})")
    int insert(Product product);

    /**
     * 更新商品
     */
    @Update("UPDATE product SET product_name = #{productName}, category_id = #{categoryId}, " +
            "brand_id = #{brandId}, main_image = #{mainImage}, images = #{images}, " +
            "description = #{description}, price = #{price}, stock = #{stock}, " +
            "sort = #{sort}, status = #{status} WHERE product_id = #{productId}")
    int update(Product product);

    /**
     * 更新排序
     */
    @Update("UPDATE product SET sort = #{sort} WHERE product_id = #{productId}")
    int updateSort(@Param("productId") String productId, @Param("sort") Integer sort);

    /**
     * 更新状态
     */
    @Update("UPDATE product SET status = #{status} WHERE product_id = #{productId}")
    int updateStatus(@Param("productId") String productId, @Param("status") Integer status);

    /**
     * 删除商品
     */
    @Delete("DELETE FROM product WHERE product_id = #{productId}")
    int deleteById(@Param("productId") String productId);

    // 在 ProductMapper 中添加以下方法

    /**
     * 更新商品库存
     */
    @Update("UPDATE product SET stock = #{stock} WHERE product_id = #{productId}")
    int updateStock(@Param("productId") String productId, @Param("stock") Integer stock);

    /**
     * 增加商品销量
     */
    @Update("UPDATE product SET sales = sales + #{quantity} WHERE product_id = #{productId}")
    int increaseSales(@Param("productId") String productId, @Param("quantity") Integer quantity);
}
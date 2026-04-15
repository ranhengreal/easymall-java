package com.easymall.mapper;

import com.easymall.entity.po.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {

    // ==================== 查询 ====================

    /**
     * 查询所有商品（按创建时间升序，新商品在后面）
     */
    @Select("SELECT p.*, c.category_name as categoryName, b.brand_name as brandName " +
            "FROM product p " +
            "LEFT JOIN category c ON p.category_id = c.category_id " +
            "LEFT JOIN brand b ON p.brand_id = b.brand_id " +
            "ORDER BY p.create_time ASC")
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
            "main_image, images, description, price, stock, status) " +
            "VALUES (#{productId}, #{productName}, #{categoryId}, #{brandId}, " +
            "#{mainImage}, #{images}, #{description}, #{price}, #{stock}, #{status})")
    int insert(Product product);

    /**
     * 更新商品
     */
    @Update("UPDATE product SET product_name = #{productName}, category_id = #{categoryId}, " +
            "brand_id = #{brandId}, main_image = #{mainImage}, images = #{images}, " +
            "description = #{description}, price = #{price}, stock = #{stock}, " +
            "status = #{status} WHERE product_id = #{productId}")
    int update(Product product);

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

    /**
     * 统计商品总数
     */
    @Select("SELECT COUNT(*) FROM product")
    Integer countAll();

    /**
     * 获取热销商品排行
     */
    @Select("SELECT p.product_id, p.product_name, p.main_image, SUM(oi.quantity) as total_sales " +
            "FROM product p " +
            "JOIN order_item oi ON p.product_id = oi.product_id " +
            "JOIN orders o ON oi.order_id = o.order_id " +
            "WHERE o.order_status = 3 " +
            "GROUP BY p.product_id " +
            "ORDER BY total_sales DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectHotProducts(@Param("limit") int limit);
}
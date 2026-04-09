package com.easymall.mapper;

import com.easymall.entity.po.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    // ==================== 查询 ====================

    /**
     * 查询所有分类（按排序升序）
     */
    @Select("SELECT category_id, category_name, p_category_id, sort FROM category ORDER BY sort ASC")
    List<Category> selectAll();

    /**
     * 根据ID查询分类
     */
    @Select("SELECT category_id, category_name, p_category_id, sort FROM category WHERE category_id = #{categoryId}")
    Category selectById(@Param("categoryId") String categoryId);

    /**
     * 根据父ID查询子分类
     */
    @Select("SELECT category_id, category_name, p_category_id, sort FROM category WHERE p_category_id = #{parentId} ORDER BY sort ASC")
    List<Category> selectByParentId(@Param("parentId") String parentId);

    /**
     * 根据名称查询分类
     */
    @Select("SELECT category_id, category_name, p_category_id, sort FROM category WHERE category_name = #{name}")
    Category selectByName(@Param("name") String name);

    /**
     * 获取最大分类ID
     */
    @Select("SELECT MAX(category_id) FROM category")
    String getMaxCategoryId();

    /**
     * 获取指定父分类下的最大排序号
     */
    @Select("SELECT MAX(sort) FROM category WHERE p_category_id = #{parentId}")
    Integer getMaxSortByParent(@Param("parentId") String parentId);

    /**
     * 检查是否有子分类
     */
    @Select("SELECT COUNT(*) FROM category WHERE p_category_id = #{categoryId}")
    int countChildren(@Param("categoryId") String categoryId);

    // ==================== 增删改 ====================

    /**
     * 新增分类
     */
    @Insert("INSERT INTO category (category_id, category_name, p_category_id, sort) " +
            "VALUES (#{categoryId}, #{categoryName}, #{pCategoryId}, #{sort})")
    int insert(Category category);

    /**
     * 更新分类
     */
    @Update("UPDATE category SET category_name = #{categoryName}, " +
            "p_category_id = #{pCategoryId}, sort = #{sort} " +
            "WHERE category_id = #{categoryId}")
    int update(Category category);

    /**
     * 更新排序
     */
    @Update("UPDATE category SET sort = #{sort} WHERE category_id = #{categoryId}")
    int updateSort(Category category);

    /**
     * 更新父分类
     */
    @Update("UPDATE category SET p_category_id = #{pCategoryId} WHERE category_id = #{categoryId}")
    int updateParent(Category category);

    /**
     * 移动后重新排序（偏移量）
     */
    @Update("UPDATE category SET sort = sort + #{offset} " +
            "WHERE p_category_id = #{parentId} AND sort >= #{startSort}")
    int shiftSortAfter(@Param("parentId") String parentId,
                       @Param("startSort") Integer startSort,
                       @Param("offset") Integer offset);

    /**
     * 删除分类
     */
    @Delete("DELETE FROM category WHERE category_id = #{categoryId}")
    int deleteById(@Param("categoryId") String categoryId);

    /**
     * 批量删除
     */
    @Delete("<script>" +
            "DELETE FROM category WHERE category_id IN " +
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDelete(@Param("list") List<String> categoryIds);
}
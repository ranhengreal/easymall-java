package com.easymall.mapper;

import com.easymall.entity.po.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BrandMapper {

    // ==================== 查询 ====================

    /**
     * 查询所有品牌（按排序升序）
     */
    @Select("SELECT * FROM brand ORDER BY sort ASC")
    List<Brand> selectAll();

    /**
     * 查询启用的品牌（用于前端下拉选择）
     */
    @Select("SELECT * FROM brand WHERE status = 1 ORDER BY sort ASC")
    List<Brand> selectEnabled();

    /**
     * 根据ID查询品牌
     */
    @Select("SELECT * FROM brand WHERE brand_id = #{brandId}")
    Brand selectById(@Param("brandId") String brandId);

    /**
     * 根据名称查询品牌（用于重名检查）
     */
    @Select("SELECT * FROM brand WHERE brand_name = #{name}")
    Brand selectByName(@Param("name") String name);

    /**
     * 获取最大品牌ID
     */
    @Select("SELECT MAX(brand_id) FROM brand")
    String getMaxBrandId();

    // ==================== 增删改 ====================

    /**
     * 新增品牌
     */
    @Insert("INSERT INTO brand (brand_id, brand_name, brand_logo, description, sort, status) " +
            "VALUES (#{brandId}, #{brandName}, #{brandLogo}, #{description}, #{sort}, #{status})")
    int insert(Brand brand);

    /**
     * 更新品牌
     */
    @Update("UPDATE brand SET brand_name = #{brandName}, brand_logo = #{brandLogo}, " +
            "description = #{description}, sort = #{sort}, status = #{status} " +
            "WHERE brand_id = #{brandId}")
    int update(Brand brand);

    /**
     * 更新排序
     */
    @Update("UPDATE brand SET sort = #{sort} WHERE brand_id = #{brandId}")
    int updateSort(Brand brand);

    /**
     * 更新状态
     */
    @Update("UPDATE brand SET status = #{status} WHERE brand_id = #{brandId}")
    int updateStatus(@Param("brandId") String brandId, @Param("status") Integer status);

    /**
     * 删除品牌
     */
    @Delete("DELETE FROM brand WHERE brand_id = #{brandId}")
    int deleteById(@Param("brandId") String brandId);

    /**
     * 批量删除
     */
    @Delete("<script>" +
            "DELETE FROM brand WHERE brand_id IN " +
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchDelete(@Param("list") List<String> brandIds);
}

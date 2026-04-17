package com.easymall.mapper;

import com.easymall.entity.po.Banner;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface BannerMapper {

    @Select("SELECT * FROM banner ORDER BY sort ASC")
    List<Banner> selectAll();

    @Select("SELECT * FROM banner WHERE status = 1 ORDER BY sort ASC")
    List<Banner> selectEnabled();

    @Insert("INSERT INTO banner (title, image_url, link_url, sort, status) " +
            "VALUES (#{title}, #{imageUrl}, #{linkUrl}, #{sort}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Banner banner);

    @Update("UPDATE banner SET title = #{title}, image_url = #{imageUrl}, " +
            "link_url = #{linkUrl}, sort = #{sort}, status = #{status} WHERE id = #{id}")
    int update(Banner banner);

    @Delete("DELETE FROM banner WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE banner SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
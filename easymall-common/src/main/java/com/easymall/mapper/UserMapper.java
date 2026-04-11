package com.easymall.mapper;

import com.easymall.entity.po.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User selectById(@Param("userId") String userId);

    @Select("SELECT MAX(user_id) FROM user")
    String getMaxUserId();

    @Insert("INSERT INTO user (user_id, username, password, nickname, phone, email, gender, status) " +
            "VALUES (#{userId}, #{username}, #{password}, #{nickname}, #{phone}, #{email}, #{gender}, #{status})")
    int insert(User user);

    @Update("UPDATE user SET nickname = #{nickname}, phone = #{phone}, " +
            "email = #{email}, gender = #{gender}, avatar = #{avatar} WHERE user_id = #{userId}")
    int updateProfile(User user);

    @Update("UPDATE user SET password = #{password} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") String userId, @Param("password") String password);

    @Update("UPDATE user SET last_login_time = NOW() WHERE user_id = #{userId}")
    int updateLastLoginTime(@Param("userId") String userId);

    /**
     * 统计用户总数
     */
    @Select("SELECT COUNT(*) FROM user")
    Integer countAll();
}
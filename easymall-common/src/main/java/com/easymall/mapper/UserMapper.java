package com.easymall.mapper;

import com.easymall.entity.po.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {

    // ==================== 查询 ====================

    /**
     * 根据用户名查询
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND is_deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询
     */
    @Select("SELECT * FROM user WHERE phone = #{phone} AND is_deleted = 0")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据ID查询
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId} AND is_deleted = 0")
    User selectById(@Param("userId") String userId);

    /**
     * 根据ID查询（包括已删除用户）
     */
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    User selectByIdIncludeDeleted(@Param("userId") String userId);

    /**
     * 条件查询用户列表（自动过滤已删除）
     */
    @Select("<script>" +
            "SELECT * FROM user WHERE is_deleted = 0" +
            "<if test='keyword != null and keyword != \"\"'> AND (username LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<User> selectByCondition(@Param("keyword") String keyword, @Param("status") Integer status);

    /**
     * 管理员查询所有用户（包括已删除）
     */
    @Select("<script>" +
            "SELECT * FROM user WHERE 1=1" +
            "<if test='keyword != null and keyword != \"\"'> AND (username LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            "<if test='status != null'> AND status = #{status}</if>" +
            "<if test='includeDeleted == null or includeDeleted == false'> AND is_deleted = 0</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<User> selectByConditionAdmin(@Param("keyword") String keyword,
                                      @Param("status") Integer status,
                                      @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 获取已删除用户列表
     */
    @Select("<script>" +
            "SELECT * FROM user WHERE is_deleted = 1" +
            "<if test='keyword != null and keyword != \"\"'> AND (username LIKE CONCAT('%', #{keyword}, '%') OR phone LIKE CONCAT('%', #{keyword}, '%'))</if>" +
            " ORDER BY create_time DESC" +
            "</script>")
    List<User> selectDeletedList(@Param("keyword") String keyword);

    /**
     * 获取最大用户ID
     */
    @Select("SELECT MAX(user_id) FROM user")
    String getMaxUserId();

    // ==================== 增删改 ====================

    /**
     * 新增用户
     */
    @Insert("INSERT INTO user (user_id, username, password, nickname, phone, email, gender, avatar, status, is_deleted, create_time) " +
            "VALUES (#{userId}, #{username}, #{password}, #{nickname}, #{phone}, #{email}, #{gender}, #{avatar}, #{status}, 0, #{createTime})")
    int insert(User user);

    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET nickname = #{nickname}, phone = #{phone}, email = #{email}, avatar = #{avatar} WHERE user_id = #{userId}")
    int updateProfile(User user);

    /**
     * 更新用户状态
     */
    @Update("UPDATE user SET status = #{status} WHERE user_id = #{userId}")
    int updateStatus(@Param("userId") String userId, @Param("status") Integer status);

    /**
     * 更新用户密码
     */
    @Update("UPDATE user SET password = #{password} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") String userId, @Param("password") String password);

    /**
     * 更新最后登录时间
     */
    @Update("UPDATE user SET last_login_time = #{loginTime} WHERE user_id = #{userId}")
    int updateLastLoginTime(@Param("userId") String userId, @Param("loginTime") LocalDateTime loginTime);

    /**
     * 逻辑删除用户
     */
    @Update("UPDATE user SET is_deleted = 1 WHERE user_id = #{userId}")
    int logicalDelete(@Param("userId") String userId);

    /**
     * 恢复用户
     */
    @Update("UPDATE user SET is_deleted = 0 WHERE user_id = #{userId}")
    int restore(@Param("userId") String userId);

    /**
     * 统计总用户数
     */
    @Select("SELECT COUNT(*) FROM user WHERE is_deleted = 0")
    Integer countAll();
}
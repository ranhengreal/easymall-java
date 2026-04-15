package com.easymall.service;

import com.easymall.entity.dto.UserDTO;
import com.easymall.entity.po.User;

import java.util.List;

public interface UserService {

    // ========== 用户端方法 ==========
    User register(UserDTO.Register dto);
    String login(UserDTO.Login dto);
    User getById(String userId);
    boolean updateProfile(String userId, UserDTO.UpdateProfile dto);
    boolean changePassword(String userId, UserDTO.ChangePassword dto);

    // ========== 管理端方法 ==========
    /**
     * 管理端获取用户列表
     */
    List<User> getAdminList(String keyword, Integer status);

    /**
     * 更新用户状态
     */
    boolean updateStatus(String userId, Integer status);

    /**
     * 重置密码
     */
    boolean resetPassword(String userId);

    /**
     * 逻辑删除用户
     */
    boolean logicalDelete(String userId);

    /**
     * 恢复用户
     */
    boolean restore(String userId);

    /**
     * 获取已删除用户列表
     */
    List<User> getDeletedList(String keyword);
}
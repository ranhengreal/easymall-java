package com.easymall.service;

import com.easymall.entity.dto.UserDTO;
import com.easymall.entity.po.User;

public interface UserService {

    User register(UserDTO.Register dto);

    String login(UserDTO.Login dto);

    User getById(String userId);

    boolean updateProfile(String userId, UserDTO.UpdateProfile dto);

    boolean changePassword(String userId, UserDTO.ChangePassword dto);
}
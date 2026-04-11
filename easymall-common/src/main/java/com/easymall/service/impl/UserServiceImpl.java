package com.easymall.service.impl;

import com.easymall.component.RedisComponent;
import com.easymall.entity.dto.UserDTO;
import com.easymall.entity.po.User;
import com.easymall.exception.BusinessException;
import com.easymall.mapper.UserMapper;
import com.easymall.service.UserService;
import com.easymall.utils.PasswordEncoder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private RedisComponent redisComponent;

    @Override
    @Transactional
    public User register(UserDTO.Register dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        if (StringUtils.hasText(dto.getPhone()) && userMapper.selectByPhone(dto.getPhone()) != null) {
            throw new BusinessException("手机号已被注册");
        }

        if (StringUtils.hasText(dto.getEmail()) && userMapper.selectByEmail(dto.getEmail()) != null) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUserId(generateUserId());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGender(0);
        user.setStatus(1);

        userMapper.insert(user);
        log.info("用户注册成功: {}", user.getUsername());
        return user;
    }

    @Override
    public String login(UserDTO.Login dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        userMapper.updateLastLoginTime(user.getUserId());
        String token = redisComponent.saveUserToken(user.getUserId(), user.getUsername());
        log.info("用户登录成功: {}", user.getUsername());
        return token;
    }

    @Override
    public User getById(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional
    public boolean updateProfile(String userId, UserDTO.UpdateProfile dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (StringUtils.hasText(dto.getPhone())) {
            User existing = userMapper.selectByPhone(dto.getPhone());
            if (existing != null && !existing.getUserId().equals(userId)) {
                throw new BusinessException("手机号已被其他用户使用");
            }
        }

        if (StringUtils.hasText(dto.getEmail())) {
            User existing = userMapper.selectByEmail(dto.getEmail());
            if (existing != null && !existing.getUserId().equals(userId)) {
                throw new BusinessException("邮箱已被其他用户使用");
            }
        }

        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setGender(dto.getGender());
        user.setAvatar(dto.getAvatar());

        return userMapper.updateProfile(user) > 0;
    }

    @Override
    @Transactional
    public boolean changePassword(String userId, UserDTO.ChangePassword dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的新密码不一致");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        String newEncodedPassword = passwordEncoder.encode(dto.getNewPassword());
        return userMapper.updatePassword(userId, newEncodedPassword) > 0;
    }

    private String generateUserId() {
        String maxId = userMapper.getMaxUserId();
        if (maxId == null) {
            return "U0000001";
        }
        try {
            int num = Integer.parseInt(maxId.substring(1));
            return String.format("U%07d", num + 1);
        } catch (NumberFormatException e) {
            return "U0000001";
        }
    }
}
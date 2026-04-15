package com.easymall.service.impl;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.dto.UserDTO;
import com.easymall.entity.po.User;
import com.easymall.exception.BusinessException;
import com.easymall.mapper.UserMapper;
import com.easymall.redis.RedisUtils;
import com.easymall.service.UserService;
import com.easymall.utils.PasswordEncoder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private PasswordEncoder passwordEncoder;

    // ========== 用户端方法 ==========

    @Override
    @Transactional
    public User register(UserDTO.Register dto) {
        // 检查用户名是否已存在
        User existing = userMapper.selectByUsername(dto.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (StringUtils.hasText(dto.getPhone())) {
            existing = userMapper.selectByPhone(dto.getPhone());
            if (existing != null) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 生成用户ID
        String userId = generateUserId();

        // 创建用户
        User user = new User();
        user.setUserId(userId);
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setStatus(Constants.USER_STATUS_ENABLED);
        user.setCreateTime(LocalDateTime.now());

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException("注册失败");
        }

        log.info("用户注册成功: userId={}, username={}", userId, dto.getUsername());
        return user;
    }

    @Override
    public String login(UserDTO.Login dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        // 检查是否被逻辑删除
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException("账号已被删除，请联系管理员");
        }

        if (user.getStatus() != Constants.USER_STATUS_ENABLED) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 生成token
        String token = UUID.randomUUID().toString();

        // 存储用户token
        redisUtils.setex(Constants.REDIS_KEY_USER_TOKEN + token, user.getUserId(), Constants.REDIS_KEY_EXPIRE_DAY * 7);

        // 更新最后登录时间
        userMapper.updateLastLoginTime(user.getUserId(), LocalDateTime.now());

        log.info("用户登录成功: userId={}, username={}", user.getUserId(), user.getUsername());
        return token;
    }

    @Override
    public User getById(String userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public boolean updateProfile(String userId, UserDTO.UpdateProfile dto) {
        User user = new User();
        user.setUserId(userId);
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());

        int result = userMapper.updateProfile(user);
        if (result > 0) {
            log.info("用户信息更新成功: userId={}", userId);
        }
        return result > 0;
    }

    @Override
    public boolean changePassword(String userId, UserDTO.ChangePassword dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        String newEncodedPassword = passwordEncoder.encode(dto.getNewPassword());
        int result = userMapper.updatePassword(userId, newEncodedPassword);

        if (result > 0) {
            log.info("用户密码修改成功: userId={}", userId);
        }
        return result > 0;
    }

    // ========== 管理端方法 ==========

    @Override
    public List<User> getAdminList(String keyword, Integer status) {
        return userMapper.selectByCondition(keyword, status);
    }

    @Override
    public boolean updateStatus(String userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        int result = userMapper.updateStatus(userId, status);
        if (result > 0) {
            log.info("管理员更新用户状态: userId={}, status={}", userId, status);
        }
        return result > 0;
    }

    @Override
    public boolean resetPassword(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String defaultPassword = passwordEncoder.encode("123456");
        int result = userMapper.updatePassword(userId, defaultPassword);

        if (result > 0) {
            log.info("管理员重置用户密码: userId={}", userId);
        }
        return result > 0;
    }

    @Override
    public boolean logicalDelete(String userId) {
        User user = userMapper.selectByIdIncludeDeleted(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getIsDeleted() != null && user.getIsDeleted() == 1) {
            throw new BusinessException("用户已被删除");
        }
        int result = userMapper.logicalDelete(userId);
        if (result > 0) {
            log.info("管理员逻辑删除用户: userId={}", userId);
        }
        return result > 0;
    }

    @Override
    public boolean restore(String userId) {
        User user = userMapper.selectByIdIncludeDeleted(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        int result = userMapper.restore(userId);
        if (result > 0) {
            log.info("管理员恢复用户: userId={}", userId);
        }
        return result > 0;
    }

    @Override
    public List<User> getDeletedList(String keyword) {
        return userMapper.selectDeletedList(keyword);
    }

    // ========== 辅助方法 ==========

    private String generateUserId() {
        String maxId = userMapper.getMaxUserId();
        if (maxId == null) {
            return "U0000001";
        }
        int num = Integer.parseInt(maxId.substring(1)) + 1;
        return String.format("U%07d", num);
    }
}
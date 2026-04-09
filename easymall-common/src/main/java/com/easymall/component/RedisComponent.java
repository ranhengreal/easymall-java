package com.easymall.component;

import com.easymall.entity.constants.Constants;
import com.easymall.redis.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    /**
     * 保存验证码数据
     */
    public String saveCheckCode(String code){
        String checkCodeKey = UUID.randomUUID().toString();
        String fullKey = Constants.REDIS_KEY_CHECK_CODE + checkCodeKey;
        redisUtils.setex(fullKey, code, 60*10);
        log.debug("保存验证码 - key: {}, code: {}", fullKey, code);
        return checkCodeKey;
    }

    /**
     * 获取验证码
     */
    public String getCheckCode(String checkCodeKey){
        String fullKey = Constants.REDIS_KEY_CHECK_CODE + checkCodeKey;
        Object value = redisUtils.get(fullKey);
        return value == null ? null : value.toString();
    }

    /**
     * 清除验证码
     */
    public void cleanCheckCode(String checkCodeKey){
        String fullKey = Constants.REDIS_KEY_CHECK_CODE + checkCodeKey;
        redisUtils.delete(fullKey);
        log.debug("清除验证码 - key: {}", fullKey);
    }

    /**
     * 保存管理员登录信息 时效一天
     */
    public String saveTokenInfoAdmin(String account){
        String token = UUID.randomUUID().toString();
        String fullKey = Constants.REDIS_KEY_TOKEN_INFO_ADMIN + token;
        redisUtils.setex(fullKey, account, Constants.REDIS_KEY_EXPIRE_DAY);
        log.info("保存管理员token - token: {}, account: {}", token, account);
        return token;
    }

    /**
     * 获取管理员信息（通过token）
     */
    public String getAdminInfoByToken(String token) {
        String fullKey = Constants.REDIS_KEY_TOKEN_INFO_ADMIN + token;
        Object value = redisUtils.get(fullKey);
        return value == null ? null : value.toString();
    }

    /**
     * 删除管理员token（登出）
     */
    public void deleteTokenInfoAdmin(String token) {
        String fullKey = Constants.REDIS_KEY_TOKEN_INFO_ADMIN + token;
        redisUtils.delete(fullKey);
        log.info("删除管理员token - token: {}", token);
    }

    // 用户相关
    public static final String REDIS_KEY_USER_TOKEN = "easymall:user:token:";

    /**
     * 保存用户登录信息
     */
    public String saveUserToken(String userId, String account) {
        String token = UUID.randomUUID().toString();
        String fullKey = REDIS_KEY_USER_TOKEN + token;
        redisUtils.setex(fullKey, userId, Constants.REDIS_KEY_EXPIRE_DAY);
        log.info("保存用户token - token: {}, userId: {}", token, userId);
        return token;
    }

    /**
     * 获取用户ID
     */
    public String getUserIdByToken(String token) {
        String fullKey = REDIS_KEY_USER_TOKEN + token;
        Object value = redisUtils.get(fullKey);
        return value == null ? null : value.toString();
    }

    /**
     * 删除用户token
     */
    public void deleteUserToken(String token) {
        String fullKey = REDIS_KEY_USER_TOKEN + token;
        redisUtils.delete(fullKey);
        log.info("删除用户token - token: {}", token);
    }
}

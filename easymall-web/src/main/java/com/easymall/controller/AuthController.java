package com.easymall.controller;

import com.easymall.component.RedisComponent;
import com.easymall.entity.result.Result;
import com.easymall.entity.result.ResultCode;
import com.easymall.entity.vo.CheckCodeVO;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Slf4j
@Validated
public class AuthController {

    @Resource
    private RedisComponent redisComponent;

    // 注意：用户登录不使用 AppConfig，应该从数据库验证

    /**
     * 获取验证码
     * GET /api/auth/checkCode
     */
    @GetMapping("/checkCode")
    public Result<CheckCodeVO> checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeBase64 = captcha.toBase64();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        CheckCodeVO checkCodeVO = new CheckCodeVO(checkCodeKey, checkCodeBase64);
        return Result.success(checkCodeVO);
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<String> login(@NotEmpty String account,
                                @NotEmpty String password,
                                @NotEmpty String checkCode,
                                @NotEmpty String checkCodeKey) {

        boolean codeValid = false;

        try {
            // 1. 验证验证码
            String savedCode = redisComponent.getCheckCode(checkCodeKey);
            if (savedCode == null) {
                return Result.error(ResultCode.CODE_EXPIRED);
            }

            if (!checkCode.equalsIgnoreCase(savedCode)) {
                return Result.error(ResultCode.CODE_ERROR);
            }

            codeValid = true;

            // 2. TODO: 从数据库验证用户账号密码
            // 暂时模拟用户登录成功
            // 实际应该从 user 表查询，校验密码
            String userId = "U0000001";  // 模拟用户ID
            String token = redisComponent.saveUserToken(userId, account);
            log.info("用户登录成功: account={}, userId={}", account, userId);
            return Result.success(token);

        } finally {
            if (codeValid) {
                redisComponent.cleanCheckCode(checkCodeKey);
            }
        }
    }

    /**
     * 用户登出
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        redisComponent.deleteUserToken(token);
        log.info("用户登出成功");
        return Result.success("登出成功");
    }

    /**
     * 从 Authorization 头中提取 token
     */
    private String extractToken(String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return authorization;
    }
}
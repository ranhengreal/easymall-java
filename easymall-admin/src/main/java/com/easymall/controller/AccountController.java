package com.easymall.controller;

import com.easymall.component.RedisComponent;
import com.easymall.entity.config.AppConfig;
import com.easymall.entity.result.Result;
import com.easymall.entity.result.ResultCode;
import com.easymall.entity.vo.CheckCodeVO;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/account")
@Slf4j
@Validated
public class AccountController {
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private AppConfig appConfig;

    // 获取验证码
    @RequestMapping("/checkCode")
    public Result<CheckCodeVO> checkCode(){
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeBase64 = captcha.toBase64();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        CheckCodeVO checkCodeVO = new CheckCodeVO(checkCodeKey, checkCodeBase64);
        return Result.success(checkCodeVO);
    }

    // 登录
    @RequestMapping("/login")
    public Result<String> login(@NotEmpty String account,
                                @NotEmpty String password,
                                @NotEmpty String checkCode,
                                @NotEmpty String checkCodeKey){

        boolean codeValid = false;

        try {
            // 1. 验证验证码
            String savedCode = redisComponent.getCheckCode(checkCodeKey);
            if(savedCode == null) {
                return Result.error(ResultCode.CODE_EXPIRED);
            }

            if(!checkCode.equalsIgnoreCase(savedCode)){
                return Result.error(ResultCode.CODE_ERROR);
            }

            // 验证码正确，标记为有效
            codeValid = true;

            // 2. 验证账号密码
            if(!account.equalsIgnoreCase(appConfig.getAdminAccount()) ||
                    !password.equalsIgnoreCase(appConfig.getAdminPassword())){
                return Result.error(ResultCode.LOGIN_ERROR);
            }

            // 3. 全部通过，生成 token
            String token = redisComponent.saveTokenInfoAdmin(account);
            log.info("管理员登录成功 - account: {}", account);
            return Result.success(token);

        } finally {
            // 只有验证码验证通过后才删除
            if(codeValid) {
                redisComponent.cleanCheckCode(checkCodeKey);
            }
        }
    }

    /**
     * 登出
     * 从请求头获取 token
     */
    @RequestMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            // 从 Authorization 头中提取 token（格式：Bearer token）
            String token = extractToken(authorization);

            if (token == null || token.isEmpty()) {
                return Result.error("请先登录");
            }

            // 检查 token 是否存在
            String account = redisComponent.getAdminInfoByToken(token);
            if (account == null) {
                return Result.error("登录已过期或无效");
            }

            // 删除 token
            redisComponent.deleteTokenInfoAdmin(token);
            log.info("管理员登出成功 - account: {}, token: {}", account, token);

            return Result.success("登出成功");

        } catch (Exception e) {
            log.error("登出失败", e);
            return Result.error("登出失败");
        }
    }
    /**
     * 从 Authorization 头中提取 token
     * Authorization 格式：Bearer xxxxx-token-xxxxx
     */
    private String extractToken(String authorization) {
        if (authorization == null || authorization.isEmpty()) {
            return null;
        }

        // 支持 "Bearer " 前缀
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        // 如果没有前缀，直接返回
        return authorization;
    }

}

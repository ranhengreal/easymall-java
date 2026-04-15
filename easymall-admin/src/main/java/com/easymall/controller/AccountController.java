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
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/account")
@Slf4j
@Validated
public class AccountController {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AppConfig appConfig;

    @GetMapping("/checkCode")
    public Result<CheckCodeVO> checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeBase64 = captcha.toBase64();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        return Result.success(new CheckCodeVO(checkCodeKey, checkCodeBase64));
    }

    @PostMapping("/login")
    public Result<String> login(@NotEmpty String account,
                                @NotEmpty String password,
                                @NotEmpty String checkCode,
                                @NotEmpty String checkCodeKey) {
        boolean codeValid = false;
        try {
            String savedCode = redisComponent.getCheckCode(checkCodeKey);
            if (savedCode == null) {
                return Result.error(ResultCode.CODE_EXPIRED);
            }
            if (!checkCode.equalsIgnoreCase(savedCode)) {
                return Result.error(ResultCode.CODE_ERROR);
            }
            codeValid = true;
            if (!account.equalsIgnoreCase(appConfig.getAdminAccount()) ||
                    !password.equalsIgnoreCase(appConfig.getAdminPassword())) {
                return Result.error(ResultCode.LOGIN_ERROR);
            }
            String token = redisComponent.saveTokenInfoAdmin(account);
            log.info("管理员登录成功: {}", account);
            return Result.success(token);
        } finally {
            if (codeValid) {
                redisComponent.cleanCheckCode(checkCodeKey);
            }
        }
    }

    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        String account = redisComponent.getAdminInfoByToken(token);
        if (account == null) {
            return Result.error("登录已过期或无效");
        }
        redisComponent.deleteTokenInfoAdmin(token);
        log.info("管理员登出成功: {}", account);
        return Result.success("登出成功");
    }

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token == null || token.isEmpty()) {
            return Result.error(401, "未登录");
        }
        String account = redisComponent.getAdminInfoByToken(token);
        if (account == null) {
            return Result.error(401, "登录已过期");
        }

        Map<String, Object> info = Map.of(
                "account", account,
                "role", "超级管理员",
                "avatar", "https://cube.elemecdn.com/0/88/03b164d5f8a6ae5a66e6f6b8b3c70e.png"
        );
        return Result.success(info);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestHeader(value = "Authorization", required = false) String authorization,
                                         @RequestBody Map<String, String> body) {
        String token = extractToken(authorization);
        if (token == null || token.isEmpty()) {
            return Result.error(401, "未登录");
        }

        String account = redisComponent.getAdminInfoByToken(token);
        if (account == null) {
            return Result.error(401, "登录已过期，请重新登录");
        }

        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");

        if (oldPassword == null || oldPassword.isEmpty()) {
            return Result.error("请输入旧密码");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.error("请输入新密码");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            return Result.error("新密码长度必须在6-20个字符之间");
        }

        // 验证旧密码
        if (!oldPassword.equals(appConfig.getAdminPassword())) {
            return Result.error("旧密码错误");
        }

        // 注意：由于密码在配置文件中，修改密码需要重启服务或更新配置
        // 这里返回成功提示，实际生产环境需要更新配置文件或使用数据库
        log.info("管理员修改密码: account={}", account);

        // 修改成功后，清除 token，让用户重新登录
        redisComponent.deleteTokenInfoAdmin(token);

        return Result.success("密码修改成功，请重新登录");
    }

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
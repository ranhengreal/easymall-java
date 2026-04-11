package com.easymall.controller;

import com.easymall.component.RedisComponent;
import com.easymall.entity.dto.UserDTO;
import com.easymall.entity.po.User;
import com.easymall.entity.result.Result;
import com.easymall.entity.result.ResultCode;
import com.easymall.entity.vo.CheckCodeVO;
import com.easymall.service.UserService;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Slf4j
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisComponent redisComponent;

    /**
     * 获取验证码
     * GET /api/user/checkCode
     */
    @GetMapping("/checkCode")
    public Result<CheckCodeVO> checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 42);
        String code = captcha.text();
        String checkCodeBase64 = captcha.toBase64();
        String checkCodeKey = redisComponent.saveCheckCode(code);
        return Result.success(new CheckCodeVO(checkCodeKey, checkCodeBase64));
    }

    /**
     * 用户注册
     * POST /api/user/register
     */
    @PostMapping("/register")
    public Result<UserDTO.Response> register(@Valid @RequestBody UserDTO.Register dto) {
        User user = userService.register(dto);
        return Result.success(UserDTO.Response.fromPO(user));
    }

    /**
     * 用户登录（需要验证码）
     * POST /api/user/login
     */
    @PostMapping("/login")
    public Result<String> login(@NotEmpty String username,
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

            // 2. 验证用户名密码
            UserDTO.Login loginDTO = new UserDTO.Login();
            loginDTO.setUsername(username);
            loginDTO.setPassword(password);

            String token = userService.login(loginDTO);
            return Result.success(token);

        } finally {
            if (codeValid) {
                redisComponent.cleanCheckCode(checkCodeKey);
            }
        }
    }

    /**
     * 用户登出
     * POST /api/user/logout
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
     * 获取当前用户信息
     * GET /api/user/info
     */
    @GetMapping("/info")
    public Result<UserDTO.Response> getInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        String token = extractToken(authorization);
        if (token == null || token.isEmpty()) {
            return Result.error(401, "未登录");
        }

        String userId = redisComponent.getUserIdByToken(token);
        if (userId == null) {
            return Result.error(401, "登录已过期");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(UserDTO.Response.fromPO(user));
    }

    /**
     * 更新个人信息
     * PUT /api/user/info
     */
    @PutMapping("/info")
    public Result<String> updateProfile(@RequestHeader(value = "Authorization", required = false) String authorization,
                                        @Valid @RequestBody UserDTO.UpdateProfile dto) {
        String token = extractToken(authorization);
        if (token == null || token.isEmpty()) {
            return Result.error(401, "未登录");
        }

        String userId = redisComponent.getUserIdByToken(token);
        if (userId == null) {
            return Result.error(401, "登录已过期");
        }

        boolean success = userService.updateProfile(userId, dto);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 修改密码
     * PUT /api/user/password
     */
    @PutMapping("/password")
    public Result<String> changePassword(@RequestHeader(value = "Authorization", required = false) String authorization,
                                         @Valid @RequestBody UserDTO.ChangePassword dto) {
        String token = extractToken(authorization);
        if (token == null || token.isEmpty()) {
            return Result.error(401, "未登录");
        }

        String userId = redisComponent.getUserIdByToken(token);
        if (userId == null) {
            return Result.error(401, "登录已过期");
        }

        boolean success = userService.changePassword(userId, dto);
        return success ? Result.success("密码修改成功") : Result.error("密码修改失败");
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
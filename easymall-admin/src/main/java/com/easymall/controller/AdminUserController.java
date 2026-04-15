package com.easymall.controller;

import com.easymall.entity.dto.UserDTO;
import com.easymall.entity.po.User;
import com.easymall.entity.result.Result;
import com.easymall.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/user")
@Slf4j
public class AdminUserController {

    @Resource
    private UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Result<List<UserDTO.Response>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {

        List<User> users = userService.getAdminList(keyword, status);
        List<UserDTO.Response> response = users.stream()
                .map(UserDTO.Response::fromPO)
                .collect(Collectors.toList());
        return Result.success(response);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    public Result<UserDTO.Response> getById(@PathVariable String userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(UserDTO.Response.fromPO(user));
    }

    /**
     * 更新用户状态（禁用/启用）
     */
    @PutMapping("/{userId}/status")
    public Result<String> updateStatus(@PathVariable String userId,
                                       @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("管理员更新用户状态: userId={}, status={}", userId, status);

        boolean success = userService.updateStatus(userId, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    /**
     * 重置密码
     */
    @PutMapping("/{userId}/reset-password")
    public Result<String> resetPassword(@PathVariable String userId) {
        log.info("管理员重置用户密码: userId={}", userId);

        boolean success = userService.resetPassword(userId);
        if (success) {
            return Result.success("密码已重置为 123456");
        }
        return Result.error("密码重置失败");
    }

    /**
     * 逻辑删除用户
     */
    @DeleteMapping("/{userId}")
    public Result<String> logicalDelete(@PathVariable String userId) {
        log.info("管理员逻辑删除用户: userId={}", userId);
        boolean success = userService.logicalDelete(userId);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 恢复用户
     */
    @PutMapping("/{userId}/restore")
    public Result<String> restore(@PathVariable String userId) {
        log.info("管理员恢复用户: userId={}", userId);
        boolean success = userService.restore(userId);
        if (success) {
            return Result.success("恢复成功");
        }
        return Result.error("恢复失败");
    }

    /**
     * 获取已删除用户列表
     */
    @GetMapping("/deleted")
    public Result<List<UserDTO.Response>> getDeletedList(
            @RequestParam(required = false) String keyword) {
        List<User> users = userService.getDeletedList(keyword);
        List<UserDTO.Response> response = users.stream()
                .map(UserDTO.Response::fromPO)
                .collect(Collectors.toList());
        return Result.success(response);
    }
}
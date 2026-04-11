package com.easymall.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.easymall.entity.po.User;
public class UserDTO {

    // ==================== 请求 DTO ====================

    @Data
    public static class Register {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
        private String username;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        private String password;

        @NotBlank(message = "确认密码不能为空")
        private String confirmPassword;

        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;

        @Email(message = "邮箱格式不正确")
        private String email;

        private String nickname;
    }

    @Data
    public static class Login {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;
    }

    @Data
    public static class UpdateProfile {
        private String nickname;
        private String phone;
        private String email;
        private Integer gender;
        private String avatar;
    }

    @Data
    public static class ChangePassword {
        @NotBlank(message = "旧密码不能为空")
        private String oldPassword;

        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        private String newPassword;

        @NotBlank(message = "确认密码不能为空")
        private String confirmPassword;
    }

    // ==================== 响应 DTO ====================

    @Data
    public static class Response {
        private String userId;
        private String username;
        private String nickname;
        private String avatar;
        private String phone;
        private String email;
        private Integer gender;
        private Integer status;
        private LocalDateTime lastLoginTime;
        private LocalDateTime createTime;

        public static Response fromPO(User po) {
            if (po == null) return null;
            Response response = new Response();
            response.setUserId(po.getUserId());
            response.setUsername(po.getUsername());
            response.setNickname(po.getNickname());
            response.setAvatar(po.getAvatar());
            response.setPhone(po.getPhone());
            response.setEmail(po.getEmail());
            response.setGender(po.getGender());
            response.setStatus(po.getStatus());
            response.setLastLoginTime(po.getLastLoginTime());
            response.setCreateTime(po.getCreateTime());
            return response;
        }
    }
}
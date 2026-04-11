package com.easymall.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String userId;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String phone;
    private String email;
    private Integer gender;
    private Integer status;
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
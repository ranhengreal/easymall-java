package com.easymall.entity.po;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private String userId;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private String email;
    private Integer gender;
    private String avatar;
    private Integer status;
    private Integer isDeleted;  // 新增：逻辑删除字段，0-未删除，1-已删除
    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
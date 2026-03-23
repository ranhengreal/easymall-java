package com.easymall.entity.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 通用错误
    ERROR(500, "操作失败"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),

    // 业务错误（1000-1999 登录相关）
    LOGIN_ERROR(1001, "账号或密码错误"),
    CODE_ERROR(1002, "验证码错误"),
    CODE_EXPIRED(1003, "验证码已过期"),
    ACCOUNT_LOCKED(1004, "账号已锁定"),
    ACCOUNT_DISABLED(1005, "账号已禁用"),
    // 登出相关
    LOGOUT_SUCCESS(200, "登出成功"),
    LOGOUT_ERROR(1006, "登出失败"),
    NOT_LOGIN(1007, "未登录"),
    TOKEN_EXPIRED(1008, "登录已过期"),

    // 商品相关（2000-2999）
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(2002, "商品库存不足"),

    // 订单相关（3000-3999）
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

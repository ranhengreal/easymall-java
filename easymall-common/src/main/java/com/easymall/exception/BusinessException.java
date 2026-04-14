package com.easymall.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于表示业务逻辑错误（如：订单未支付、库存不足等）
 */
@Getter
public class BusinessException extends RuntimeException {

    private Integer code;

    /**
     * 业务异常，默认使用 400 状态码
     */
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    /**
     * 自定义状态码的业务异常
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 业务异常，带原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }
}
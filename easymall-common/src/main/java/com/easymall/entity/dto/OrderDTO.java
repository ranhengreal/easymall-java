package com.easymall.entity.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单相关 DTO 统一管理
 */
public class OrderDTO {

    // ==================== 请求 DTO ====================

    /**
     * 创建订单请求
     */
    @Data
    public static class Create {
        @NotBlank(message = "收货人姓名不能为空")
        @Size(min = 2, max = 50, message = "收货人姓名长度必须在2-50个字符之间")
        private String receiverName;

        @NotBlank(message = "收货人电话不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
        private String receiverPhone;

        private String receiverProvince;
        private String receiverCity;
        private String receiverDistrict;

        @NotBlank(message = "详细地址不能为空")
        @Size(max = 200, message = "详细地址不能超过200个字符")
        private String receiverAddress;

        private String receiverZip;
        private String userNote;

        @NotNull(message = "订单商品不能为空")
        @Size(min = 1, message = "至少需要一个商品")
        private List<OrderItemCreate> items;

        @Min(value = 1, message = "支付方式值范围1-3")
        @Max(value = 3, message = "支付方式值范围1-3")
        private Integer payType = 1;
    }

    /**
     * 订单商品创建请求
     */
    @Data
    public static class OrderItemCreate {
        @NotBlank(message = "商品ID不能为空")
        private String productId;

        private String skuId;

        @NotNull(message = "购买数量不能为空")
        @Min(value = 1, message = "购买数量不能小于1")
        @Max(value = 999, message = "购买数量不能大于999")
        private Integer quantity;
    }

    /**
     * 更新订单请求（统一接口）
     */
    @Data
    public static class Update {
        // 订单状态（0-待付款，1-待发货，2-待收货，3-已完成，4-已取消，5-售后中）
        @Min(value = 0, message = "订单状态值范围0-5")
        @Max(value = 5, message = "订单状态值范围0-5")
        private Integer orderStatus;

        // 支付状态（0-未支付，1-已支付，2-已退款）
        @Min(value = 0, message = "支付状态值范围0-2")
        @Max(value = 2, message = "支付状态值范围0-2")
        private Integer payStatus;

        // 支付方式（1-微信，2-支付宝，3-余额）
        @Min(value = 1, message = "支付方式值范围1-3")
        @Max(value = 3, message = "支付方式值范围1-3")
        private Integer payType;

        // 取消原因（取消订单时使用）
        private String cancelReason;

        // 注意：payTime 由后端自动生成，前端不需要传递
    }

    /**
     * 订单查询请求
     */
    @Data
    public static class Query {
        private String orderSn;
        private String userId;
        private Integer orderStatus;
        private Integer payStatus;
        private String startTime;
        private String endTime;
        private Integer pageNum = 1;
        private Integer pageSize = 10;
    }

    // ==================== 响应 DTO ====================

    /**
     * 订单响应
     */
    @Data
    public static class Response {
        private String orderId;
        private String orderSn;
        private String userId;
        private String userName;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal freightAmount;
        private BigDecimal payAmount;
        private Integer payType;
        private Integer payStatus;
        private LocalDateTime payTime;
        private Integer orderStatus;
        private String orderStatusName;
        private String receiverName;
        private String receiverPhone;
        private String receiverProvince;
        private String receiverCity;
        private String receiverDistrict;
        private String receiverAddress;
        private String receiverZip;
        private String userNote;
        private String cancelReason;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private List<OrderItemResponse> items;

        public static Response fromPO(com.easymall.entity.po.Order po) {
            if (po == null) return null;
            Response response = new Response();
            response.setOrderId(po.getOrderId());
            response.setOrderSn(po.getOrderSn());
            response.setUserId(po.getUserId());
            response.setUserName(po.getUserName());
            response.setTotalAmount(po.getTotalAmount());
            response.setDiscountAmount(po.getDiscountAmount());
            response.setFreightAmount(po.getFreightAmount());
            response.setPayAmount(po.getPayAmount());
            response.setPayType(po.getPayType());
            response.setPayStatus(po.getPayStatus());
            response.setPayTime(po.getPayTime());
            response.setOrderStatus(po.getOrderStatus());
            response.setReceiverName(po.getReceiverName());
            response.setReceiverPhone(po.getReceiverPhone());
            response.setReceiverProvince(po.getReceiverProvince());
            response.setReceiverCity(po.getReceiverCity());
            response.setReceiverDistrict(po.getReceiverDistrict());
            response.setReceiverAddress(po.getReceiverAddress());
            response.setReceiverZip(po.getReceiverZip());
            response.setUserNote(po.getUserNote());
            response.setCancelReason(po.getCancelReason());
            response.setCreateTime(po.getCreateTime());
            response.setUpdateTime(po.getUpdateTime());

            // 设置订单状态名称
            String[] statusNames = {"待付款", "待发货", "待收货", "已完成", "已取消", "售后中"};
            if (po.getOrderStatus() != null && po.getOrderStatus() >= 0 && po.getOrderStatus() <= 5) {
                response.setOrderStatusName(statusNames[po.getOrderStatus()]);
            }

            return response;
        }
    }

    /**
     * 订单商品响应
     */
    @Data
    public static class OrderItemResponse {
        private String itemId;
        private String orderId;
        private String productId;
        private String productName;
        private String productImage;
        private String skuId;
        private String specValues;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal totalAmount;
        private LocalDateTime createTime;
    }
}
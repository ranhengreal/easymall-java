package com.easymall.entity.po;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
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
    private String remark;


    private String logisticsCompany;  // 物流公司
    private String trackingNumber;    // 物流单号
    private LocalDateTime cancelTime; // 取消时间
    private LocalDateTime shipTime;   // 发货时间
    private LocalDateTime receiveTime;// 收货时间
    // 关联字段
    private List<OrderItem> items;
}
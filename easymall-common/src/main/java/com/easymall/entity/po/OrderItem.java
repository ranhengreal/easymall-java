package com.easymall.entity.po;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderItem {
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
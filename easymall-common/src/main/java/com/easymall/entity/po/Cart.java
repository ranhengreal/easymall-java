package com.easymall.entity.po;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Cart {
    private String cartId;
    private String userId;
    private String productId;
    private String productName;
    private String productImage;
    private String skuId;
    private String specValues;
    private BigDecimal price;
    private Integer quantity;
    private Integer selected;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
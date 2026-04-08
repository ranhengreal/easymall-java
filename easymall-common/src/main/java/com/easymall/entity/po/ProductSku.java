package com.easymall.entity.po;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSku {
    private String skuId;
    private String productId;
    private String specValues;
    private BigDecimal price;
    private Integer stock;
    private String image;
}
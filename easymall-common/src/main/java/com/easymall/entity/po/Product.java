package com.easymall.entity.po;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Product {
    private String productId;
    private String productName;
    private String categoryId;
    private String brandId;
    private String mainImage;
    private String images;  // JSON 字符串
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer sales;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联字段（非数据库字段）
    private String categoryName;
    private String brandName;
    private List<ProductSku> skuList;
}
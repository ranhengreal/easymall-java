package com.easymall.entity.dto;

import com.easymall.entity.po.Product;
import com.easymall.entity.po.ProductSku;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品相关 DTO 统一管理
 */
public class ProductDTO {

    // ==================== 请求 DTO ====================

    /**
     * 新增商品请求
     */
    @Data
    public static class Add {
        @NotBlank(message = "商品名称不能为空")
        @Size(min = 2, max = 200, message = "商品名称长度必须在2-200个字符之间")
        private String productName;

        @NotBlank(message = "分类ID不能为空")
        private String categoryId;

        private String brandId;

        private String mainImage;

        private String images;

        private String description;

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格不能小于0.01")
        private BigDecimal price;

        @NotNull(message = "库存不能为空")
        @Min(value = 0, message = "库存不能小于0")
        private Integer stock;

        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort = 0;

        @Min(value = 0, message = "状态值只能是0或1")
        @Max(value = 1, message = "状态值只能是0或1")
        private Integer status = 1;

        private List<SkuAdd> skuList;
    }

    /**
     * 更新商品请求
     */
    @Data
    public static class Update {
        private String productId;

        @NotBlank(message = "商品名称不能为空")
        @Size(min = 2, max = 200, message = "商品名称长度必须在2-200个字符之间")
        private String productName;

        @NotBlank(message = "分类ID不能为空")
        private String categoryId;

        private String brandId;

        private String mainImage;

        private String images;

        private String description;

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格不能小于0.01")
        private BigDecimal price;

        @NotNull(message = "库存不能为空")
        @Min(value = 0, message = "库存不能小于0")
        private Integer stock;

        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort = 0;

        @Min(value = 0, message = "状态值只能是0或1")
        @Max(value = 1, message = "状态值只能是0或1")
        private Integer status = 1;

        private List<SkuUpdate> skuList;
    }

    /**
     * 新增SKU请求
     */
    @Data
    public static class SkuAdd {
        @NotBlank(message = "规格值不能为空")
        private String specValues;

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格不能小于0.01")
        private BigDecimal price;

        @NotNull(message = "库存不能为空")
        @Min(value = 0, message = "库存不能小于0")
        private Integer stock;

        private String image;
    }

    /**
     * 更新SKU请求
     */
    @Data
    public static class SkuUpdate {
        @NotBlank(message = "SKU ID不能为空")
        private String skuId;

        @NotBlank(message = "规格值不能为空")
        private String specValues;

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0.01", message = "价格不能小于0.01")
        private BigDecimal price;

        @NotNull(message = "库存不能为空")
        @Min(value = 0, message = "库存不能小于0")
        private Integer stock;

        private String image;
    }

    /**
     * 批量更新排序请求
     */
    @Data
    public static class Sort {
        @NotBlank(message = "商品ID不能为空")
        private String productId;

        @NotNull(message = "排序不能为空")
        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort;
    }

    // ==================== 响应 DTO ====================

    /**
     * 商品响应
     */
    @Data
    public static class Response {
        private String productId;
        private String productName;
        private String categoryId;
        private String categoryName;
        private String brandId;
        private String brandName;
        private String mainImage;
        private String images;
        private String description;
        private BigDecimal price;
        private Integer stock;
        private Integer sales;
        private Integer sort;
        private Integer status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private List<SkuResponse> skuList;

        public static Response fromPO(Product po) {
            if (po == null) return null;
            Response response = new Response();
            response.setProductId(po.getProductId());
            response.setProductName(po.getProductName());
            response.setCategoryId(po.getCategoryId());
            response.setBrandId(po.getBrandId());
            response.setMainImage(po.getMainImage());
            response.setImages(po.getImages());
            response.setDescription(po.getDescription());
            response.setPrice(po.getPrice());
            response.setStock(po.getStock());
            response.setSales(po.getSales());
            response.setSort(po.getSort());
            response.setStatus(po.getStatus());
            response.setCreateTime(po.getCreateTime());
            response.setUpdateTime(po.getUpdateTime());
            return response;
        }
    }

    /**
     * SKU响应
     */
    @Data
    public static class SkuResponse {
        private String skuId;
        private String productId;
        private String specValues;
        private BigDecimal price;
        private Integer stock;
        private String image;
    }
}
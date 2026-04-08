package com.easymall.entity.dto;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.po.Brand;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 品牌相关 DTO 统一管理
 */
public class BrandDTO {

    // ==================== 请求 DTO ====================

    /**
     * 新增品牌请求
     */
    @Data
    public static class Add {
        @NotBlank(message = "品牌名称不能为空")
        @Size(min = 2, max = 50, message = "品牌名称长度必须在2-50个字符之间")
        private String brandName;

        private String brandLogo;

        @Size(max = 500, message = "品牌描述不能超过500个字符")
        private String description;

        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort = 0;

        @Min(value = 0, message = "状态值只能是0或1")
        @Max(value = 1, message = "状态值只能是0或1")
        private Integer status = 1;
    }

    /**
     * 更新品牌请求（品牌ID通过路径参数传递）
     */
    @Data
    public static class Update {
        @NotBlank(message = "品牌名称不能为空")
        @Size(min = 2, max = 50, message = "品牌名称长度必须在2-50个字符之间")
        private String brandName;

        private String brandLogo;

        @Size(max = 500, message = "品牌描述不能超过500个字符")
        private String description;

        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort = 0;

        @Min(value = 0, message = "状态值只能是0或1")
        @Max(value = 1, message = "状态值只能是0或1")
        private Integer status = 1;
    }

    /**
     * 批量更新排序请求
     */
    @Data
    public static class Sort {
        @NotBlank(message = "品牌ID不能为空")
        private String brandId;

        @NotNull(message = "排序不能为空")
        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort;
    }

    /**
     * 批量删除请求
     */
    @Data
    public static class BatchDelete {
        @NotEmpty(message = "品牌ID列表不能为空")
        private List<String> brandIds;
    }

    // ==================== 响应 DTO ====================

    /**
     * 品牌响应
     */
    @Data
    public static class Response {
        private String brandId;
        private String brandName;
        private String brandLogo;
        private String description;
        private Integer sort;
        private Integer status;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;

        public static Response fromPO(Brand po) {
            if (po == null) return null;
            Response response = new Response();
            response.setBrandId(po.getBrandId());
            response.setBrandName(po.getBrandName());
            response.setBrandLogo(po.getBrandLogo());
            response.setDescription(po.getDescription());
            response.setSort(po.getSort());
            response.setStatus(po.getStatus());
            response.setCreateTime(po.getCreateTime());
            response.setUpdateTime(po.getUpdateTime());
            return response;
        }

        public static List<Response> fromPOList(List<Brand> poList) {
            if (poList == null) return null;
            return poList.stream()
                    .map(Response::fromPO)
                    .collect(java.util.stream.Collectors.toList());
        }
    }
}
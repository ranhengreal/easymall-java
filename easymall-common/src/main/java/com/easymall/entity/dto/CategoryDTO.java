package com.easymall.entity.dto;

import com.easymall.entity.constants.Constants;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 分类相关 DTO 统一管理
 */
public class CategoryDTO {

    // ==================== 请求 DTO ====================

    /**
     * 新增分类请求
     */
    @Data
    public static class Add {
        @NotBlank(message = "分类名称不能为空")
        @Size(min = 2, max = 50, message = "分类名称长度必须在2-50个字符之间")
        private String categoryName;

        private String pCategoryId = Constants.CATEGORY_ROOT_PARENT_ID;

        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort = Constants.CATEGORY_DEFAULT_SORT;
    }

    /**
     * 更新分类请求
     */
    @Data
    public static class Update {
        @NotBlank(message = "分类ID不能为空")
        private String categoryId;

        @NotBlank(message = "分类名称不能为空")
        @Size(min = 2, max = 50, message = "分类名称长度必须在2-50个字符之间")
        private String categoryName;

        private String pCategoryId = Constants.CATEGORY_ROOT_PARENT_ID;

        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort = Constants.CATEGORY_DEFAULT_SORT;
    }

    /**
     * 批量更新排序请求
     */
    @Data
    public static class Sort {
        @NotBlank(message = "分类ID不能为空")
        private String categoryId;

        @NotNull(message = "排序不能为空")
        @Min(value = 0, message = "排序不能小于0")
        @Max(value = 999, message = "排序不能大于999")
        private Integer sort;
    }

    /**
     * 移动分类请求
     */
    @Data
    public static class Move {
        @NotBlank(message = "分类ID不能为空")
        private String categoryId;

        @NotBlank(message = "目标分类ID不能为空")
        private String targetCategoryId;

        @NotBlank(message = "移动类型不能为空")
        @Pattern(regexp = "before|after|inner", message = "移动类型必须是 before、after 或 inner")
        private String moveType;
    }

    /**
     * 批量删除请求
     */
    @Data
    public static class BatchDelete {
        @NotEmpty(message = "分类ID列表不能为空")
        private List<String> categoryIds;

        private boolean recursive = false;
    }

    // ==================== 响应 DTO ====================

    /**
     * 分类响应（树形结构）
     */
    @Data
    public static class Response {
        private String categoryId;
        private String categoryName;
        private String pCategoryId;
        private Integer sort;
        private List<Response> children;

        public static Response fromPO(com.easymall.entity.po.Category po) {
            if (po == null) return null;
            Response response = new Response();
            response.setCategoryId(po.getCategoryId());
            response.setCategoryName(po.getCategoryName());
            response.setPCategoryId(po.getPCategoryId());
            response.setSort(po.getSort());

            if (po.getChildren() != null && !po.getChildren().isEmpty()) {
                List<Response> children = po.getChildren().stream()
                        .map(Response::fromPO)
                        .collect(java.util.stream.Collectors.toList());
                response.setChildren(children);
            }
            return response;
        }

        public static List<Response> fromPOList(List<com.easymall.entity.po.Category> poList) {
            if (poList == null) return null;
            return poList.stream()
                    .map(Response::fromPO)
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    /**
     * 分类平铺响应（不包含 children）
     */
    @Data
    public static class SimpleResponse {
        private String categoryId;
        private String categoryName;
        private String pCategoryId;
        private Integer sort;

        public static SimpleResponse fromPO(com.easymall.entity.po.Category po) {
            if (po == null) return null;
            SimpleResponse response = new SimpleResponse();
            response.setCategoryId(po.getCategoryId());
            response.setCategoryName(po.getCategoryName());
            response.setPCategoryId(po.getPCategoryId());
            response.setSort(po.getSort());
            return response;
        }

        public static List<SimpleResponse> fromPOList(List<com.easymall.entity.po.Category> poList) {
            if (poList == null) return null;
            return poList.stream()
                    .map(SimpleResponse::fromPO)
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    /**
     * 分类路径响应
     */
    @Data
    public static class PathResponse {
        private String categoryId;
        private String fullPath;
        private List<String> pathIds;
        private List<String> pathNames;

        public static PathResponse of(String categoryId, String fullPath,
                                      List<String> pathIds, List<String> pathNames) {
            PathResponse response = new PathResponse();
            response.setCategoryId(categoryId);
            response.setFullPath(fullPath);
            response.setPathIds(pathIds);
            response.setPathNames(pathNames);
            return response;
        }
    }
}
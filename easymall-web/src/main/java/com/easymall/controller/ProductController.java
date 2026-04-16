package com.easymall.controller;

import com.easymall.entity.dto.ProductDTO;
import com.easymall.entity.po.Product;
import com.easymall.entity.result.PageResult;
import com.easymall.entity.result.Result;
import com.easymall.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 商品列表（支持分页、搜索、筛选、排序）
     * GET /api/product/list?pageNum=1&pageSize=12&categoryId=C001&keyword=手机&orderBy=price_asc
     */
    @GetMapping("/list")
    public Result<PageResult<ProductDTO.Response>> getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String orderBy) {

        // 1. 获取所有上架商品
        List<Product> allProducts = productService.getList().stream()
                .filter(p -> p.getStatus() == 1)  // 只显示上架商品
                .collect(Collectors.toList());

        // 2. 按分类筛选
        if (categoryId != null && !categoryId.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> categoryId.equals(p.getCategoryId()))
                    .collect(Collectors.toList());
        }

        // 3. 按关键词搜索（商品名称）
        if (keyword != null && !keyword.isEmpty()) {
            allProducts = allProducts.stream()
                    .filter(p -> p.getProductName().contains(keyword))
                    .collect(Collectors.toList());
        }

        // 4. 排序
        if (orderBy != null && !orderBy.isEmpty()) {
            switch (orderBy) {
                case "price_asc":
                    allProducts.sort((a, b) -> a.getPrice().compareTo(b.getPrice()));
                    break;
                case "price_desc":
                    allProducts.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
                    break;
                case "sales":
                    allProducts.sort((a, b) -> b.getSales().compareTo(a.getSales()));
                    break;
                default:
                    // 默认按创建时间倒序
                    allProducts.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
                    break;
            }
        } else {
            // 默认按创建时间倒序
            allProducts.sort((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()));
        }

        // 5. 分页
        int total = allProducts.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Product> pageList = allProducts.subList(start, end);
        List<ProductDTO.Response> responseList = pageList.stream()
                .map(ProductDTO.Response::fromPO)
                .collect(Collectors.toList());

        // 6. 返回分页结果
        PageResult<ProductDTO.Response> pageResult = new PageResult<>(
                responseList, (long) total, pageNum, pageSize
        );

        return Result.success(pageResult);
    }

    /**
     * 商品详情
     * GET /api/product/{productId}
     */
    @GetMapping("/{productId}")
    public Result<ProductDTO.Response> getById(@PathVariable String productId) {
        Product product = productService.getById(productId);
        if (product == null || product.getStatus() != 1) {
            return Result.error(404, "商品不存在");
        }
        return Result.success(ProductDTO.Response.fromPO(product));
    }
}
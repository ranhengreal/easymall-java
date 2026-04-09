package com.easymall.controller;

import com.easymall.entity.dto.ProductDTO;
import com.easymall.entity.po.Product;
import com.easymall.entity.result.Result;
import com.easymall.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")  // 实际路径：/api/product/xxx
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 商品列表
     * GET /api/product/list
     */
    @GetMapping("/list")
    public Result<List<ProductDTO.Response>> getList() {
        List<Product> list = productService.getList();
        List<ProductDTO.Response> response = list.stream()
                .filter(p -> p.getStatus() == 1)
                .map(ProductDTO.Response::fromPO)
                .toList();
        return Result.success(response);
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
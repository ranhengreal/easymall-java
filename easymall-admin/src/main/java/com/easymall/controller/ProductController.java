package com.easymall.controller;

import com.easymall.entity.dto.ProductDTO;
import com.easymall.entity.po.Product;
import com.easymall.entity.po.ProductSku;
import com.easymall.entity.result.Result;
import com.easymall.service.ProductService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/product")
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    // ==================== 查询接口 ====================

    @GetMapping
    public Result<List<ProductDTO.Response>> getList() {
        List<Product> list = productService.getList();
        List<ProductDTO.Response> response = list.stream()
                .map(ProductDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    @GetMapping("/{productId}")
    public Result<ProductDTO.Response> getById(@PathVariable String productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        ProductDTO.Response response = ProductDTO.Response.fromPO(product);

        if (product.getSkuList() != null) {
            List<ProductDTO.SkuResponse> skuResponses = product.getSkuList().stream()
                    .map(sku -> {
                        ProductDTO.SkuResponse skuResp = new ProductDTO.SkuResponse();
                        skuResp.setSkuId(sku.getSkuId());
                        skuResp.setProductId(sku.getProductId());
                        skuResp.setSpecValues(sku.getSpecValues());
                        skuResp.setPrice(sku.getPrice());
                        skuResp.setStock(sku.getStock());
                        skuResp.setImage(sku.getImage());
                        return skuResp;
                    })
                    .toList();
            response.setSkuList(skuResponses);
        }

        return Result.success(response);
    }

    // ==================== 增删改接口 ====================

    @PostMapping
    public Result<String> add(@Valid @RequestBody ProductDTO.Add dto) {
        Product product = new Product();
        product.setProductName(dto.getProductName());
        product.setCategoryId(dto.getCategoryId());
        product.setBrandId(dto.getBrandId());
        product.setMainImage(dto.getMainImage());
        product.setImages(dto.getImages());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setStatus(dto.getStatus());

        if (dto.getSkuList() != null && !dto.getSkuList().isEmpty()) {
            List<ProductSku> skuList = dto.getSkuList().stream()
                    .map(skuDto -> {
                        ProductSku sku = new ProductSku();
                        sku.setSpecValues(skuDto.getSpecValues());
                        sku.setPrice(skuDto.getPrice());
                        sku.setStock(skuDto.getStock());
                        sku.setImage(skuDto.getImage());
                        return sku;
                    })
                    .toList();
            product.setSkuList(skuList);
        }

        boolean success = productService.add(product);
        if (success) {
            log.info("新增商品成功: {}", dto.getProductName());
            return Result.success("新增成功");
        }
        return Result.error("新增失败");
    }

    @PutMapping("/{productId}")
    public Result<String> update(@PathVariable String productId,
                                 @Valid @RequestBody ProductDTO.Update dto) {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(dto.getProductName());
        product.setCategoryId(dto.getCategoryId());
        product.setBrandId(dto.getBrandId());
        product.setMainImage(dto.getMainImage());
        product.setImages(dto.getImages());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setSort(dto.getSort());
        product.setStatus(dto.getStatus());

        if (dto.getSkuList() != null && !dto.getSkuList().isEmpty()) {
            List<ProductSku> skuList = dto.getSkuList().stream()
                    .map(skuDto -> {
                        ProductSku sku = new ProductSku();
                        if (skuDto.getSkuId() != null && !skuDto.getSkuId().isEmpty()) {
                            sku.setSkuId(skuDto.getSkuId());
                        }
                        sku.setSpecValues(skuDto.getSpecValues());
                        sku.setPrice(skuDto.getPrice());
                        sku.setStock(skuDto.getStock());
                        sku.setImage(skuDto.getImage());
                        return sku;
                    })
                    .toList();
            product.setSkuList(skuList);
        }

        boolean success = productService.update(product);
        if (success) {
            log.info("更新商品成功: {}", productId);
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 更新商品状态（单独接口）
     * PUT /admin/product/{productId}/status
     */
    @PutMapping("/{productId}/status")
    public Result<String> updateStatus(@PathVariable String productId,
                                       @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        log.info("更新商品状态: productId={}, status={}", productId, status);

        boolean success = productService.updateStatus(productId, status);
        if (success) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    @DeleteMapping("/{productId}")
    public Result<String> delete(@PathVariable String productId) {
        boolean success = productService.delete(productId);
        if (success) {
            log.info("删除商品成功: {}", productId);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
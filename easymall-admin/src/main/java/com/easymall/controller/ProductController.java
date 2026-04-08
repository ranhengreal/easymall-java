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

@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Resource
    private ProductService productService;

    // ==================== 查询接口 ====================

    /**
     * 获取商品列表
     * GET /product
     */
    @GetMapping
    public Result<List<ProductDTO.Response>> getList() {
        List<Product> list = productService.getList();
        List<ProductDTO.Response> response = list.stream()
                .map(ProductDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    /**
     * 获取商品详情
     * GET /product/{productId}
     */
    @GetMapping("/{productId}")
    public Result<ProductDTO.Response> getById(@PathVariable String productId) {
        Product product = productService.getById(productId);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        ProductDTO.Response response = ProductDTO.Response.fromPO(product);

        // 填充SKU列表
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

    /**
     * 新增商品
     * POST /product
     */
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
        product.setSort(dto.getSort());
        product.setStatus(dto.getStatus());

        // 转换SKU
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

    /**
     * 更新商品（包含状态更新）
     * PUT /product/{productId}
     */
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
        product.setStatus(dto.getStatus());  // 状态在这里一起更新

        // 转换SKU
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
     * 删除商品
     * DELETE /product/{productId}
     */
    @DeleteMapping("/{productId}")
    public Result<String> delete(@PathVariable String productId) {
        boolean success = productService.delete(productId);
        if (success) {
            log.info("删除商品成功: {}", productId);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    // ==================== 排序接口 ====================

    /**
     * 批量更新排序
     * PUT /product/sort/batch
     */
    @PutMapping("/sort/batch")
    public Result<String> batchUpdateSort(@Valid @RequestBody List<ProductDTO.Sort> sortList) {
        productService.batchUpdateSort(sortList);
        return Result.success("排序更新成功");
    }
}
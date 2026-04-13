package com.easymall.service.impl;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.po.Product;
import com.easymall.entity.po.ProductSku;
import com.easymall.exception.BusinessException;
import com.easymall.mapper.ProductMapper;
import com.easymall.mapper.ProductSkuMapper;
import com.easymall.redis.RedisUtils;
import com.easymall.service.ProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Resource
    private RedisUtils redisUtils;

    // ==================== 查询实现 ====================

    @Override
    public List<Product> getList() {
        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<Product> cached = (List<Product>) redisUtils.get(Constants.REDIS_KEY_PRODUCT_LIST);
        if (cached != null && !cached.isEmpty()) {
            log.debug("从缓存获取商品列表");
            return cached;
        }

        log.debug("缓存未命中，从数据库查询");
        List<Product> list = productMapper.selectAll();

        // 为每个商品加载SKU信息
        if (list != null && !list.isEmpty()) {
            for (Product product : list) {
                List<ProductSku> skuList = productSkuMapper.selectByProductId(product.getProductId());
                product.setSkuList(skuList);
            }
        }

        // 存入缓存
        if (list != null && !list.isEmpty()) {
            redisUtils.setex(Constants.REDIS_KEY_PRODUCT_LIST, list, Constants.REDIS_KEY_EXPIRE_HOUR);
        }

        return list != null ? list : List.of();
    }

    @Override
    public Product getById(String productId) {
        String cacheKey = Constants.REDIS_KEY_PRODUCT + productId;
        Product cached = (Product) redisUtils.get(cacheKey);
        if (cached != null) {
            log.debug("从缓存获取商品详情: {}", productId);
            return cached;
        }

        log.debug("缓存未命中，从数据库查询: {}", productId);
        Product product = productMapper.selectById(productId);
        if (product != null) {
            // 加载SKU信息
            List<ProductSku> skuList = productSkuMapper.selectByProductId(productId);
            product.setSkuList(skuList);
            redisUtils.setex(cacheKey, product, Constants.REDIS_KEY_EXPIRE_HOUR);
        }
        return product;
    }

    // ==================== 增删改实现 ====================

    @Override
    @Transactional
    public boolean add(Product product) {
        // 生成商品ID
        if (!StringUtils.hasText(product.getProductId())) {
            product.setProductId(generateProductId());
        }

        // 校验
        validateProduct(product);

        // 插入商品
        int result = productMapper.insert(product);

        if (result > 0) {
            // 插入SKU - 使用UUID生成唯一ID
            if (product.getSkuList() != null && !product.getSkuList().isEmpty()) {
                for (ProductSku sku : product.getSkuList()) {
                    sku.setSkuId(generateSkuId());
                    sku.setProductId(product.getProductId());
                }
                productSkuMapper.batchInsert(product.getSkuList());
            }

            clearCache();
            log.info("新增商品成功: {} - {}", product.getProductId(), product.getProductName());
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean update(Product product) {
        // 检查是否存在
        Product existing = productMapper.selectById(product.getProductId());
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }

        // 校验名称是否重复（排除自身）
        validateProductForUpdate(product);

        // 更新商品
        int result = productMapper.update(product);

        if (result > 0) {
            // 处理SKU：先删除原有的，再重新插入
            productSkuMapper.deleteByProductId(product.getProductId());

            if (product.getSkuList() != null && !product.getSkuList().isEmpty()) {
                for (ProductSku sku : product.getSkuList()) {
                    if (!StringUtils.hasText(sku.getSkuId())) {
                        sku.setSkuId(generateSkuId());
                    }
                    sku.setProductId(product.getProductId());
                }
                productSkuMapper.batchInsert(product.getSkuList());
            }

            clearCache();
            clearProductCache(product.getProductId());
            log.info("更新商品成功: {} - {}, 状态: {}",
                    product.getProductId(),
                    product.getProductName(),
                    product.getStatus() == 1 ? "上架" : "下架");
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean delete(String productId) {
        // 检查是否存在
        Product existing = productMapper.selectById(productId);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }

        // 删除SKU
        productSkuMapper.deleteByProductId(productId);

        // 删除商品
        int result = productMapper.deleteById(productId);

        if (result > 0) {
            clearCache();
            clearProductCache(productId);
            log.info("删除商品成功: {}", productId);
        }

        return result > 0;
    }

    // ==================== 辅助方法 ====================

    /**
     * 新增时校验商品
     */
    private void validateProduct(Product product) {
        // 检查名称是否重复
        int count = productMapper.countByName(product.getProductName());
        if (count > 0) {
            throw new BusinessException("商品名称已存在");
        }
    }

    /**
     * 更新时校验商品（排除自身）
     */
    private void validateProductForUpdate(Product product) {
        Product existing = productMapper.selectById(product.getProductId());
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查名称是否重复（排除自身）
        if (!existing.getProductName().equals(product.getProductName())) {
            int count = productMapper.countByName(product.getProductName());
            if (count > 0) {
                throw new BusinessException("商品名称已存在");
            }
        }
    }

    /**
     * 生成商品ID（格式：P000001）
     */
    private String generateProductId() {
        String maxId = productMapper.getMaxProductId();
        if (maxId == null) {
            return "P000001";
        }

        try {
            int num = Integer.parseInt(maxId.substring(1));
            return String.format("P%06d", num + 1);
        } catch (NumberFormatException e) {
            log.warn("解析商品ID失败: {}", maxId);
            return "P000001";
        }
    }

    /**
     * 生成SKU ID（使用UUID，保证唯一）
     */
    private String generateSkuId() {
        return "SKU" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    /**
     * 清除商品列表缓存
     */
    private void clearCache() {
        redisUtils.delete(Constants.REDIS_KEY_PRODUCT_LIST);
        log.debug("清除商品列表缓存");
    }

    /**
     * 清除单个商品缓存
     */
    private void clearProductCache(String productId) {
        String cacheKey = Constants.REDIS_KEY_PRODUCT + productId;
        redisUtils.delete(cacheKey);
        log.debug("清除商品缓存: {}", cacheKey);
    }
}
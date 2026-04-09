package com.easymall.service.impl;

import com.easymall.entity.po.Cart;
import com.easymall.entity.po.Product;
import com.easymall.entity.po.ProductSku;
import com.easymall.exception.BusinessException;
import com.easymall.mapper.CartMapper;
import com.easymall.mapper.ProductMapper;
import com.easymall.mapper.ProductSkuMapper;
import com.easymall.service.CartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Override
    public List<Cart> getList(String userId) {
        return cartMapper.selectByUserId(userId);
    }

    @Override
    public List<Cart> getSelectedList(String userId) {
        return cartMapper.selectSelectedByUserId(userId);
    }

    @Override
    @Transactional
    public boolean add(Cart cart, String userId) {
        // 检查商品是否存在
        Product product = productMapper.selectById(cart.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        // 检查SKU是否存在
        if (StringUtils.hasText(cart.getSkuId())) {
            ProductSku sku = productSkuMapper.selectBySkuId(cart.getSkuId());
            if (sku == null) {
                throw new BusinessException("商品规格不存在");
            }
            cart.setSpecValues(sku.getSpecValues());
            cart.setPrice(sku.getPrice());
        } else {
            cart.setPrice(product.getPrice());
        }

        cart.setUserId(userId);
        cart.setProductName(product.getProductName());
        cart.setProductImage(product.getMainImage());
        cart.setSelected(1);

        // 检查购物车是否已有该商品
        Cart existing = cartMapper.selectByProduct(userId, cart.getProductId(), cart.getSkuId());
        if (existing != null) {
            // 已存在，更新数量
            int newQuantity = existing.getQuantity() + cart.getQuantity();
            return cartMapper.updateQuantity(existing.getCartId(), newQuantity) > 0;
        } else {
            // 不存在，新增
            cart.setCartId(generateCartId());
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            int result = cartMapper.insert(cart);
            if (result > 0) {
                log.info("添加购物车成功: userId={}, productId={}", userId, cart.getProductId());
            }
            return result > 0;
        }
    }

    @Override
    public boolean updateQuantity(String cartId, Integer quantity) {
        if (quantity <= 0) {
            return cartMapper.deleteById(cartId) > 0;
        }
        return cartMapper.updateQuantity(cartId, quantity) > 0;
    }

    @Override
    public boolean updateSelected(String cartId, Integer selected) {
        return cartMapper.updateSelected(cartId, selected) > 0;
    }

    @Override
    public void batchUpdateSelected(List<String> cartIds, Integer selected) {
        if (cartIds == null || cartIds.isEmpty()) {
            return;
        }
        cartMapper.batchUpdateSelected(cartIds, selected);
        log.info("批量更新购物车选中状态: {}条", cartIds.size());
    }

    @Override
    public boolean delete(String cartId) {
        int result = cartMapper.deleteById(cartId);
        if (result > 0) {
            log.info("删除购物车商品: {}", cartId);
        }
        return result > 0;
    }

    @Override
    public void batchDelete(List<String> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return;
        }
        cartMapper.batchDelete(cartIds);
        log.info("批量删除购物车商品: {}条", cartIds.size());
    }

    @Override
    public void clear(String userId) {
        cartMapper.clearByUserId(userId);
        log.info("清空购物车: userId={}", userId);
    }

    /**
     * 生成购物车ID
     */
    private String generateCartId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "CART" + timestamp;
    }
}
package com.easymall.service.impl;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.dto.OrderDTO;
import com.easymall.entity.po.Order;
import com.easymall.entity.po.OrderItem;
import com.easymall.entity.po.Product;
import com.easymall.entity.po.ProductSku;
import com.easymall.exception.BusinessException;
import com.easymall.mapper.OrderItemMapper;
import com.easymall.mapper.OrderMapper;
import com.easymall.mapper.ProductMapper;
import com.easymall.mapper.ProductSkuMapper;
import com.easymall.redis.RedisUtils;
import com.easymall.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private ProductSkuMapper productSkuMapper;

    @Resource
    private RedisUtils redisUtils;

    // ==================== 查询实现 ====================

    @Override
    public List<Order> getList() {
        // 尝试从缓存获取
        @SuppressWarnings("unchecked")
        List<Order> cached = (List<Order>) redisUtils.get(Constants.REDIS_KEY_ORDER_LIST);
        if (cached != null && !cached.isEmpty()) {
            log.debug("从缓存获取订单列表");
            return cached;
        }

        log.debug("缓存未命中，从数据库查询");
        List<Order> list = orderMapper.selectAll();

        // 为每个订单加载商品明细
        if (list != null && !list.isEmpty()) {
            for (Order order : list) {
                List<OrderItem> items = orderItemMapper.selectByOrderId(order.getOrderId());
                order.setItems(items);
            }
        }

        // 存入缓存
        if (list != null && !list.isEmpty()) {
            redisUtils.setex(Constants.REDIS_KEY_ORDER_LIST, list, Constants.REDIS_KEY_EXPIRE_HOUR);
        }

        return list != null ? list : List.of();
    }

    @Override
    public Order getById(String orderId) {
        String cacheKey = Constants.REDIS_KEY_ORDER + orderId;

        // 尝试从缓存获取
        try {
            Order cached = (Order) redisUtils.get(cacheKey);
            if (cached != null) {
                log.debug("从缓存获取订单详情: {}", orderId);
                return cached;
            }
        } catch (Exception e) {
            log.warn("从缓存获取订单失败，将直接从数据库读取: {}", e.getMessage());
        }

        // 从数据库查询
        log.debug("从数据库查询订单: {}", orderId);
        Order order = orderMapper.selectById(orderId);
        if (order != null) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
            order.setItems(items);

            // 尝试设置缓存
            try {
                redisUtils.setex(cacheKey, order, Constants.REDIS_KEY_EXPIRE_HOUR);
                log.debug("设置订单缓存成功: {}", orderId);
            } catch (Exception e) {
                log.warn("设置订单缓存失败: {}", e.getMessage());
            }
        }
        return order;
    }

    @Override
    public List<Order> getByUserId(String userId) {
        return orderMapper.selectByUserId(userId);
    }

    @Override
    public List<Order> query(OrderDTO.Query query) {
        // 调用修改后的 Mapper 方法
        return orderMapper.selectByCondition(
                query.getOrderSn(),
                query.getUserName(),
                query.getOrderStatus(),
                query.getPayStatus()
        );
    }

    // ==================== 增删改实现 ====================

    @Override
    @Transactional
    public Order create(OrderDTO.Create dto, String userId) {
        // 1. 生成订单ID和订单号
        String orderId = generateOrderId();
        String orderSn = generateOrderSn();

        // 2. 计算订单金额并获取商品信息
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderDTO.OrderItemCreate itemDto : dto.getItems()) {
            // 获取商品信息
            Product product = productMapper.selectById(itemDto.getProductId());
            if (product == null) {
                throw new BusinessException("商品不存在: " + itemDto.getProductId());
            }

            // 检查库存
            if (product.getStock() < itemDto.getQuantity()) {
                throw new BusinessException("商品库存不足: " + product.getProductName());
            }

            // 获取SKU信息（如果有）
            String specValues = null;
            BigDecimal skuPrice = null;
            if (StringUtils.hasText(itemDto.getSkuId())) {
                ProductSku sku = productSkuMapper.selectBySkuId(itemDto.getSkuId());
                if (sku != null) {
                    specValues = sku.getSpecValues();
                    skuPrice = sku.getPrice();
                    // 检查SKU库存
                    if (sku.getStock() < itemDto.getQuantity()) {
                        throw new BusinessException("商品SKU库存不足: " + product.getProductName() + " - " + specValues);
                    }
                }
            }

            // 计算小计金额
            BigDecimal price = skuPrice != null ? skuPrice : product.getPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // 创建订单商品明细
            OrderItem item = new OrderItem();
            item.setItemId(generateItemId());
            item.setOrderId(orderId);
            item.setProductId(product.getProductId());
            item.setProductName(product.getProductName());
            item.setProductImage(product.getMainImage());
            item.setSkuId(itemDto.getSkuId());
            item.setSpecValues(specValues);
            item.setPrice(price);
            item.setQuantity(itemDto.getQuantity());
            item.setTotalAmount(itemTotal);
            items.add(item);

            // 扣减库存
            product.setStock(product.getStock() - itemDto.getQuantity());
            productMapper.updateStock(product.getProductId(), product.getStock());

            if (StringUtils.hasText(itemDto.getSkuId())) {
                productSkuMapper.decreaseStock(itemDto.getSkuId(), itemDto.getQuantity());
            }
        }

        // 3. 创建订单
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderSn(orderSn);
        order.setUserId(userId);
        order.setUserName("用户" + userId);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);
        order.setOrderStatus(Constants.ORDER_STATUS_WAIT_PAY);
        order.setPayStatus(Constants.PAY_STATUS_UNPAID);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverProvince(dto.getReceiverProvince());
        order.setReceiverCity(dto.getReceiverCity());
        order.setReceiverDistrict(dto.getReceiverDistrict());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setReceiverZip(dto.getReceiverZip());
        order.setUserNote(dto.getUserNote());

        // 4. 保存订单
        int result = orderMapper.insert(order);
        if (result > 0) {
            orderItemMapper.batchInsert(items);
            clearCache();
            log.info("创建订单成功: {} - 金额: {}", orderId, totalAmount);
        }

        return order;
    }

    @Override
    @Transactional
    public boolean updateStatus(String orderId, Integer orderStatus, String cancelReason) {
        Order existing = orderMapper.selectById(orderId);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }

        // 校验状态流转
        validateStatusTransition(existing.getOrderStatus(), orderStatus);

        int result = orderMapper.updateStatus(orderId, orderStatus, cancelReason);

        if (result > 0) {
            clearCache();
            clearOrderCache(orderId);
            log.info("更新订单状态成功: {} -> {}", orderId, orderStatus);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean updatePayStatus(String orderId, Integer payStatus, Integer payType) {
        Order existing = orderMapper.selectById(orderId);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }

        // 已支付的订单不能重复支付
        if (existing.getPayStatus() == Constants.PAY_STATUS_PAID && payStatus == Constants.PAY_STATUS_PAID) {
            throw new BusinessException("订单已支付，请勿重复操作");
        }

        // 支付时间自动生成
        LocalDateTime payTime = payStatus == Constants.PAY_STATUS_PAID ? LocalDateTime.now() : null;

        int result = orderMapper.updatePayStatus(orderId, payStatus, payTime, payType);

        // 支付成功后更新订单状态为待发货
        if (result > 0 && payStatus == Constants.PAY_STATUS_PAID) {
            orderMapper.updateStatus(orderId, Constants.ORDER_STATUS_WAIT_SHIP, null);
        }

        if (result > 0) {
            clearCache();
            clearOrderCache(orderId);
            log.info("更新支付状态成功: {} -> {}", orderId, payStatus == 1 ? "已支付" : "未支付");
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean cancel(String orderId, String cancelReason) {
        Order existing = orderMapper.selectById(orderId);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }

        // 只有待付款的订单可以取消
        if (existing.getOrderStatus() != Constants.ORDER_STATUS_WAIT_PAY) {
            throw new BusinessException("当前订单状态无法取消");
        }

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateStock(product.getProductId(), product.getStock());
            }
            if (StringUtils.hasText(item.getSkuId())) {
                productSkuMapper.increaseStock(item.getSkuId(), item.getQuantity());
            }
        }

        int result = orderMapper.updateStatus(orderId, Constants.ORDER_STATUS_CANCELLED, cancelReason);

        if (result > 0) {
            clearCache();
            clearOrderCache(orderId);
            log.info("取消订单成功: {}", orderId);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean confirmReceive(String orderId) {
        Order existing = orderMapper.selectById(orderId);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }

        if (existing.getOrderStatus() != Constants.ORDER_STATUS_WAIT_RECEIVE) {
            throw new BusinessException("当前订单状态无法确认收货");
        }

        int result = orderMapper.updateStatus(orderId, Constants.ORDER_STATUS_COMPLETED, null);

        if (result > 0) {
            clearCache();
            clearOrderCache(orderId);
            log.info("确认收货成功: {}", orderId);
        }

        return result > 0;
    }

    @Override
    @Transactional
    public boolean delete(String orderId) {
        Order existing = orderMapper.selectById(orderId);
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }

        // 修改：允许删除已完成(3)或已取消(4)的订单
        if (existing.getOrderStatus() != Constants.ORDER_STATUS_COMPLETED
                && existing.getOrderStatus() != Constants.ORDER_STATUS_CANCELLED) {
            throw new BusinessException("只有已完成或已取消的订单才能删除");
        }

        // 删除订单商品明细
        orderItemMapper.deleteByOrderId(orderId);

        // 删除订单
        int result = orderMapper.deleteById(orderId);

        if (result > 0) {
            clearCache();
            clearOrderCache(orderId);
            log.info("删除订单成功: {}", orderId);
        }

        return result > 0;
    }

    // ==================== 辅助方法 ====================

    /**
     * 校验订单状态流转
     */
    private void validateStatusTransition(Integer currentStatus, Integer newStatus) {
        switch (currentStatus) {
            case 0: // 待付款
                if (newStatus != 1 && newStatus != 4) {
                    throw new BusinessException("待付款订单只能转为待发货或已取消");
                }
                break;
            case 1: // 待发货
                if (newStatus != 2) {
                    throw new BusinessException("待发货订单只能转为待收货");
                }
                break;
            case 2: // 待收货
                if (newStatus != 3) {
                    throw new BusinessException("待收货订单只能转为已完成");
                }
                break;
            case 3: // 已完成
                if (newStatus != 5) {
                    throw new BusinessException("已完成订单只能转为售后中");
                }
                break;
            case 4: // 已取消
                throw new BusinessException("已取消订单不能修改状态");
            case 5: // 售后中
                throw new BusinessException("售后中订单请通过售后流程处理");
            default:
                throw new BusinessException("未知的订单状态");
        }
    }

    @Override
    public int updateRemark(String orderId, String remark) {
        int result = orderMapper.updateRemark(orderId, remark);
        if (result > 0) {
            // 清除订单缓存
            clearOrderCache(orderId);
            // 清除订单列表缓存
            clearCache();
            log.info("更新备注成功并清除缓存: orderId={}", orderId);
        }
        return result;
    }

    /**
     * 生成订单ID
     */
    private String generateOrderId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "ORD" + timestamp;
    }

    /**
     * 生成订单号
     */
    private String generateOrderSn() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "SN" + timestamp;
    }

    /**
     * 生成订单明细ID
     */
    private String generateItemId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        return "ITM" + timestamp;
    }

    /**
     * 订单发货
     */
    @Override
    @Transactional
    public boolean ship(String orderId, String logisticsCompany, String trackingNumber) {
        try {
            // 1. 查询订单
            Order existing = orderMapper.selectById(orderId);
            if (existing == null) {
                throw new BusinessException("订单不存在");
            }

            // 2. 校验：只有待发货的订单才能发货
            if (existing.getOrderStatus() != Constants.ORDER_STATUS_WAIT_SHIP) {
                throw new BusinessException("只有待发货的订单才能发货，当前状态：" + getStatusText(existing.getOrderStatus()));
            }

            // 3. 校验：只有已支付的订单才能发货
            if (existing.getPayStatus() != Constants.PAY_STATUS_PAID) {
                throw new BusinessException("订单未支付，无法发货");
            }

            // 4. 调用 Mapper 中专门的发货方法
            int result = orderMapper.ship(orderId, logisticsCompany, trackingNumber);
            if (result > 0) {
                clearCache();
                clearOrderCache(orderId);
                log.info("发货成功: orderId={}, 物流公司={}, 物流单号={}",
                        orderId, logisticsCompany, trackingNumber);
            }
            return result > 0;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("发货失败", e);
            throw new BusinessException("发货失败：" + e.getMessage());
        }
    }
    /**
     * 获取状态文本（用于错误提示）
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "待收货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "售后中";
            default: return "未知";
        }
    }
    private void clearCache() {
        redisUtils.delete(Constants.REDIS_KEY_ORDER_LIST);
    }

    private void clearOrderCache(String orderId) {
        redisUtils.delete(Constants.REDIS_KEY_ORDER + orderId);
    }
}
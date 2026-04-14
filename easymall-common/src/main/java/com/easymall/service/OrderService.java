package com.easymall.service;

import com.easymall.entity.dto.OrderDTO;
import com.easymall.entity.po.Order;

import java.util.List;

public interface OrderService {

    // ==================== 查询 ====================

    /**
     * 获取所有订单列表
     */
    List<Order> getList();

    /**
     * 根据ID获取订单（包含商品明细）
     */
    Order getById(String orderId);

    /**
     * 根据用户ID获取订单列表
     */
    List<Order> getByUserId(String userId);

    /**
     * 分页条件查询订单
     */
    List<Order> query(OrderDTO.Query query);

    // ==================== 操作 ====================

    /**
     * 创建订单
     */
    Order create(OrderDTO.Create dto, String userId);

    /**
     * 更新订单状态
     */
    boolean updateStatus(String orderId, Integer orderStatus, String cancelReason);

    /**
     * 更新支付状态（支付时间自动生成）
     */
    boolean updatePayStatus(String orderId, Integer payStatus, Integer payType);

    /**
     * 取消订单
     */
    boolean cancel(String orderId, String cancelReason);

    /**
     * 确认收货
     */
    boolean confirmReceive(String orderId);

    /**
     * 删除订单
     */
    boolean delete(String orderId);

    /**
     * 更新订单备注
     */
    int updateRemark(String orderId, String remark);

    /**
     * 订单发货
     */
    boolean ship(String orderId, String logisticsCompany, String trackingNumber);

}
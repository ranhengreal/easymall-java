package com.easymall.controller;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.dto.OrderDTO;
import com.easymall.entity.po.Order;
import com.easymall.entity.result.Result;
import com.easymall.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    // ==================== 查询接口 ====================

    /**
     * 获取所有订单列表（管理员接口）
     * GET /order
     */
    @GetMapping
    public Result<List<OrderDTO.Response>> getList() {
        List<Order> list = orderService.getList();
        List<OrderDTO.Response> response = list.stream()
                .map(OrderDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    /**
     * 获取订单详情
     * GET /order/{orderId}
     */
    @GetMapping("/{orderId}")
    public Result<OrderDTO.Response> getById(@PathVariable String orderId,
                                             @RequestHeader(value = "userId", required = false) String userId,
                                             @RequestHeader(value = "role", required = false) String role) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 如果不是管理员，需要校验订单归属
        boolean isAdmin = "admin".equals(role);
        if (!isAdmin && userId != null && !order.getUserId().equals(userId)) {
            return Result.error(403, "无权查看此订单");
        }

        OrderDTO.Response response = OrderDTO.Response.fromPO(order);

        if (order.getItems() != null) {
            List<OrderDTO.OrderItemResponse> items = order.getItems().stream()
                    .map(item -> {
                        OrderDTO.OrderItemResponse itemResp = new OrderDTO.OrderItemResponse();
                        itemResp.setItemId(item.getItemId());
                        itemResp.setOrderId(item.getOrderId());
                        itemResp.setProductId(item.getProductId());
                        itemResp.setProductName(item.getProductName());
                        itemResp.setProductImage(item.getProductImage());
                        itemResp.setSkuId(item.getSkuId());
                        itemResp.setSpecValues(item.getSpecValues());
                        itemResp.setPrice(item.getPrice());
                        itemResp.setQuantity(item.getQuantity());
                        itemResp.setTotalAmount(item.getTotalAmount());
                        itemResp.setCreateTime(item.getCreateTime());
                        return itemResp;
                    })
                    .toList();
            response.setItems(items);
        }

        return Result.success(response);
    }

    /**
     * 获取当前用户的订单列表（用户接口）
     * GET /order/user
     */
    @GetMapping("/user")
    public Result<List<OrderDTO.Response>> getMyOrders(@RequestHeader(value = "userId", required = true) String userId) {
        List<Order> list = orderService.getByUserId(userId);
        List<OrderDTO.Response> response = list.stream()
                .map(OrderDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    /**
     * 条件查询订单（支持多条件筛选）
     * GET /order/list?orderStatus=1&startTime=2024-01-01&endTime=2024-12-31
     */
    @GetMapping("/list")
    public Result<List<OrderDTO.Response>> queryByCondition(
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) Integer payStatus,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "role", required = false) String role) {

        // 普通用户只能查自己的订单
        boolean isAdmin = "admin".equals(role);
        if (!isAdmin && userId == null) {
            // 非管理员且没有传userId，无法查询
            return Result.error(403, "无权查询");
        }

        OrderDTO.Query query = new OrderDTO.Query();
        query.setOrderSn(orderSn);
        query.setUserId(isAdmin ? userId : null); // 管理员可以指定userId，普通用户不能
        query.setOrderStatus(orderStatus);
        query.setPayStatus(payStatus);
        query.setStartTime(startTime);
        query.setEndTime(endTime);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        List<Order> list = orderService.query(query);
        List<OrderDTO.Response> response = list.stream()
                .map(OrderDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    // ==================== 操作接口 ====================

    /**
     * 创建订单
     * POST /order
     */
    @PostMapping
    public Result<OrderDTO.Response> create(@Valid @RequestBody OrderDTO.Create dto,
                                            @RequestHeader(value = "userId", required = true) String userId) {
        Order order = orderService.create(dto, userId);
        log.info("创建订单成功: userId={}, orderId={}", userId, order.getOrderId());
        return Result.success(OrderDTO.Response.fromPO(order));
    }

    /**
     * 更新订单（统一接口）
     * PUT /order/{orderId}
     */
    @PutMapping("/{orderId}")
    public Result<String> update(@PathVariable String orderId,
                                 @Valid @RequestBody OrderDTO.Update dto,
                                 @RequestHeader(value = "userId", required = true) String userId) {

        // 校验订单是否属于当前用户
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }

        // 1. 如果有支付状态变更，更新支付状态
        if (dto.getPayStatus() != null) {
            orderService.updatePayStatus(orderId, dto.getPayStatus(), dto.getPayType());
        }

        // 2. 如果有订单状态变更，更新订单状态
        if (dto.getOrderStatus() != null) {
            if (dto.getOrderStatus() == Constants.ORDER_STATUS_CANCELLED) {
                orderService.cancel(orderId, dto.getCancelReason());
            } else if (dto.getOrderStatus() == Constants.ORDER_STATUS_COMPLETED) {
                orderService.confirmReceive(orderId);
            } else {
                orderService.updateStatus(orderId, dto.getOrderStatus(), dto.getCancelReason());
            }
        }

        log.info("更新订单成功: userId={}, orderId={}", userId, orderId);
        return Result.success("更新成功");
    }

    /**
     * 删除订单
     * DELETE /order/{orderId}
     */
    @DeleteMapping("/{orderId}")
    public Result<String> delete(@PathVariable String orderId,
                                 @RequestHeader(value = "userId", required = true) String userId) {
        // 校验订单是否属于当前用户
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权删除此订单");
        }

        boolean success = orderService.delete(orderId);
        if (success) {
            log.info("删除订单成功: userId={}, orderId={}", userId, orderId);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
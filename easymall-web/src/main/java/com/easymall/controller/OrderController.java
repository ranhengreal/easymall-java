// easymall-web/src/main/java/com/easymall/controller/OrderController.java
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
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    // ==================== 用户端查询接口 ====================

    /**
     * 获取我的订单列表
     */
    @GetMapping("/my")
    public Result<List<OrderDTO.Response>> getMyOrders(@RequestHeader("userId") String userId) {
        List<Order> list = orderService.getByUserId(userId);
        List<OrderDTO.Response> response = list.stream()
                .map(OrderDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    /**
     * 获取订单详情（用户端需校验归属）
     */
    @GetMapping("/{orderId}")
    public Result<OrderDTO.Response> getById(@PathVariable String orderId,
                                             @RequestHeader("userId") String userId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 校验订单归属
        if (!order.getUserId().equals(userId)) {
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

    // ==================== 用户端操作接口 ====================

    /**
     * 创建订单
     */
    @PostMapping
    public Result<OrderDTO.Response> create(@Valid @RequestBody OrderDTO.Create dto,
                                            @RequestHeader("userId") String userId) {
        Order order = orderService.create(dto, userId);
        log.info("用户创建订单成功: userId={}, orderId={}", userId, order.getOrderId());
        return Result.success(OrderDTO.Response.fromPO(order));
    }

    /**
     * 取消订单（用户）
     */
    @PutMapping("/{orderId}/cancel")
    public Result<String> cancel(@PathVariable String orderId,
                                 @RequestHeader("userId") String userId,
                                 @RequestBody(required = false) Map<String, String> body) {
        log.info("用户取消订单: userId={}, orderId={}", userId, orderId);

        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 校验订单归属
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }

        // 只有待付款的订单可以取消
        if (order.getOrderStatus() != Constants.ORDER_STATUS_WAIT_PAY) {
            return Result.error(400, "当前订单状态无法取消");
        }

        String cancelReason = body != null ? body.get("cancelReason") : "用户取消";
        boolean success = orderService.cancel(orderId, cancelReason);
        if (success) {
            return Result.success("订单已取消");
        }
        return Result.error("取消失败");
    }

    /**
     * 确认收货
     */
    @PutMapping("/{orderId}/confirm")
    public Result<String> confirmReceive(@PathVariable String orderId,
                                         @RequestHeader("userId") String userId) {
        log.info("用户确认收货: userId={}, orderId={}", userId, orderId);

        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 校验订单归属
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }

        // 只有待收货的订单可以确认收货
        if (order.getOrderStatus() != Constants.ORDER_STATUS_WAIT_RECEIVE) {
            return Result.error(400, "当前订单状态无法确认收货");
        }

        boolean success = orderService.confirmReceive(orderId);
        if (success) {
            return Result.success("确认收货成功");
        }
        return Result.error("确认收货失败");
    }

    /**
     * 支付回调（更新支付状态）
     */
    @PutMapping("/{orderId}/pay")
    public Result<String> updatePayStatus(@PathVariable String orderId,
                                          @RequestBody Map<String, Object> body,
                                          @RequestHeader("userId") String userId) {
        Integer payStatus = (Integer) body.get("payStatus");
        Integer payType = (Integer) body.get("payType");

        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }

        boolean success = orderService.updatePayStatus(orderId, payStatus, payType);
        if (success) {
            return Result.success("支付状态更新成功");
        }
        return Result.error("支付状态更新失败");
    }
}
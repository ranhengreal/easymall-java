package com.easymall.controller;

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
@RequestMapping("/order")  // 实际路径：/api/order/xxx
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping
    public Result<OrderDTO.Response> create(@Valid @RequestBody OrderDTO.Create dto,
                                            @RequestHeader(value = "userId", required = true) String userId) {
        Order order = orderService.create(dto, userId);
        return Result.success(OrderDTO.Response.fromPO(order));
    }

    @GetMapping("/list")
    public Result<List<OrderDTO.Response>> getMyOrders(@RequestHeader(value = "userId", required = true) String userId) {
        List<Order> list = orderService.getByUserId(userId);
        List<OrderDTO.Response> response = list.stream()
                .map(OrderDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    @GetMapping("/{orderId}")
    public Result<OrderDTO.Response> getById(@PathVariable String orderId,
                                             @RequestHeader(value = "userId", required = true) String userId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权查看此订单");
        }
        return Result.success(OrderDTO.Response.fromPO(order));
    }

    @PutMapping("/{orderId}/cancel")
    public Result<String> cancel(@PathVariable String orderId,
                                 @RequestParam(required = false) String cancelReason,
                                 @RequestHeader(value = "userId", required = true) String userId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }
        boolean success = orderService.cancel(orderId, cancelReason);
        return success ? Result.success("取消成功") : Result.error("取消失败");
    }

    @PutMapping("/{orderId}/confirm")
    public Result<String> confirm(@PathVariable String orderId,
                                  @RequestHeader(value = "userId", required = true) String userId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权操作此订单");
        }
        boolean success = orderService.confirmReceive(orderId);
        return success ? Result.success("确认收货成功") : Result.error("确认收货失败");
    }

    @DeleteMapping("/{orderId}")
    public Result<String> delete(@PathVariable String orderId,
                                 @RequestHeader(value = "userId", required = true) String userId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权删除此订单");
        }
        boolean success = orderService.delete(orderId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
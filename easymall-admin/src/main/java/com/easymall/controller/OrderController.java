package com.easymall.controller;

import com.easymall.entity.constants.Constants;
import com.easymall.entity.dto.OrderDTO;
import com.easymall.entity.po.Order;
import com.easymall.entity.result.Result;
import com.easymall.service.OrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {

    @Resource
    private OrderService orderService;

    // ==================== 管理端查询接口 ====================

    /**
     * 获取所有订单列表
     */
    @GetMapping("/list")
    public Result<List<OrderDTO.Response>> queryByCondition(
            @RequestParam(required = false) String orderSn,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) Integer orderStatus) {

        OrderDTO.Query query = new OrderDTO.Query();
        query.setOrderSn(orderSn);
        query.setUserName(userName);
        query.setOrderStatus(orderStatus);

        List<Order> list = orderService.query(query);
        List<OrderDTO.Response> response = list.stream()
                .map(OrderDTO.Response::fromPO)
                .toList();
        return Result.success(response);
    }

    /**
     * 获取订单详情（管理端无权限校验）
     */
    @GetMapping("/{orderId}")
    public Result<OrderDTO.Response> getById(@PathVariable String orderId) {
        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
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

    // ==================== 管理端操作接口 ====================

    /**
     * 订单发货
     */
    @PutMapping("/{orderId}/ship")
    public Result<String> ship(@PathVariable String orderId,
                               @RequestBody Map<String, String> body) {
        String logisticsCompany = body.get("logisticsCompany");
        String trackingNumber = body.get("trackingNumber");

        log.info("订单发货: orderId={}, 物流公司={}, 物流单号={}",
                orderId, logisticsCompany, trackingNumber);

        boolean success = orderService.ship(orderId, logisticsCompany, trackingNumber);
        if (success) {
            return Result.success("发货成功");
        }
        return Result.error("发货失败");
    }

    /**
     * 取消订单（管理员）
     */
    @PutMapping("/{orderId}/cancel")
    public Result<String> cancel(@PathVariable String orderId,
                                 @RequestBody(required = false) Map<String, String> body) {
        String cancelReason = body != null ? body.get("cancelReason") : "管理员取消";
        log.info("管理员取消订单: orderId={}, 原因={}", orderId, cancelReason);

        boolean success = orderService.cancel(orderId, cancelReason);
        if (success) {
            return Result.success("订单已取消");
        }
        return Result.error("取消失败");
    }

    /**
     * 更新订单备注
     */
    @PutMapping("/{orderId}/remark")
    public Result<String> updateRemark(@PathVariable String orderId,
                                       @RequestBody Map<String, String> body) {
        String remark = body.get("remark");
        log.info("更新订单备注: orderId={}, remark={}", orderId, remark);

        int result = orderService.updateRemark(orderId, remark);
        if (result > 0) {
            return Result.success("备注更新成功");
        }
        return Result.error("备注更新失败");
    }

    /**
     * 管理员删除订单
     */
    @DeleteMapping("/{orderId}")
    public Result<String> delete(@PathVariable String orderId) {
        log.info("管理员删除订单: orderId={}", orderId);

        Order order = orderService.getById(orderId);
        if (order == null) {
            return Result.error(404, "订单不存在");
        }

        // 只有已完成或已取消的订单才能删除
        if (order.getOrderStatus() != Constants.ORDER_STATUS_COMPLETED
                && order.getOrderStatus() != Constants.ORDER_STATUS_CANCELLED) {
            return Result.error(400, "只有已完成或已取消的订单才能删除");
        }

        boolean success = orderService.delete(orderId);
        if (success) {
            log.info("管理员删除订单成功: {}", orderId);
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
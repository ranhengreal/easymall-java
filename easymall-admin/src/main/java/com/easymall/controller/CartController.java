package com.easymall.controller;

import com.easymall.entity.dto.OrderDTO;
import com.easymall.entity.po.Cart;
import com.easymall.entity.result.Result;
import com.easymall.service.CartService;
import com.easymall.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/cart")
@Slf4j
public class CartController {

    @Resource
    private CartService cartService;

    @Resource
    private OrderService orderService;

    // ==================== 购物车操作 ====================

    /**
     * 获取购物车列表
     * GET /cart
     */
    @GetMapping
    public Result<List<Cart>> getList(@RequestHeader(value = "userId", required = true) String userId) {
        List<Cart> list = cartService.getList(userId);
        return Result.success(list);
    }

    /**
     * 添加商品到购物车
     * POST /cart/add
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Cart cart,
                              @RequestHeader(value = "userId", required = true) String userId) {
        cart.setQuantity(cart.getQuantity() == null ? 1 : cart.getQuantity());
        boolean success = cartService.add(cart, userId);
        if (success) {
            log.info("添加购物车成功: userId={}, productId={}", userId, cart.getProductId());
            return Result.success("添加成功");
        }
        return Result.error("添加失败");
    }

    /**
     * 更新购物车数量
     * PUT /cart/{cartId}/quantity
     */
    @PutMapping("/{cartId}/quantity")
    public Result<String> updateQuantity(@PathVariable String cartId,
                                         @RequestParam Integer quantity,
                                         @RequestHeader(value = "userId", required = true) String userId) {
        boolean success = cartService.updateQuantity(cartId, quantity);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 更新选中状态
     * PUT /cart/{cartId}/selected
     */
    @PutMapping("/{cartId}/selected")
    public Result<String> updateSelected(@PathVariable String cartId,
                                         @RequestParam Integer selected,
                                         @RequestHeader(value = "userId", required = true) String userId) {
        boolean success = cartService.updateSelected(cartId, selected);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 批量更新选中状态
     * PUT /cart/selected/batch
     */
    @PutMapping("/selected/batch")
    public Result<String> batchUpdateSelected(@RequestBody List<String> cartIds,
                                              @RequestParam Integer selected,
                                              @RequestHeader(value = "userId", required = true) String userId) {
        cartService.batchUpdateSelected(cartIds, selected);
        return Result.success("批量更新成功");
    }

    /**
     * 删除购物车商品
     * DELETE /cart/{cartId}
     */
    @DeleteMapping("/{cartId}")
    public Result<String> delete(@PathVariable String cartId,
                                 @RequestHeader(value = "userId", required = true) String userId) {
        boolean success = cartService.delete(cartId);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 批量删除购物车商品
     * DELETE /cart/batch
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<String> cartIds,
                                      @RequestHeader(value = "userId", required = true) String userId) {
        cartService.batchDelete(cartIds);
        return Result.success("批量删除成功");
    }

    /**
     * 清空购物车
     * DELETE /cart/clear
     */
    @DeleteMapping("/clear")
    public Result<String> clear(@RequestHeader(value = "userId", required = true) String userId) {
        cartService.clear(userId);
        return Result.success("清空成功");
    }

    // ==================== 结算下单 ====================

    /**
     * 从购物车结算下单
     * POST /cart/checkout
     */
    @PostMapping("/checkout")
    public Result<OrderDTO.Response> checkout(@Valid @RequestBody OrderDTO.Create dto,
                                              @RequestHeader(value = "userId", required = true) String userId) {
        // 创建订单
        com.easymall.entity.po.Order order = orderService.create(dto, userId);

        // 清空已下单的购物车商品（可选）
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OrderDTO.OrderItemCreate item : dto.getItems()) {
                // 查找并删除购物车中的对应商品
                // 这里可以根据需要实现
            }
        }

        log.info("结算下单成功: userId={}, orderId={}", userId, order.getOrderId());
        return Result.success(OrderDTO.Response.fromPO(order));
    }
}
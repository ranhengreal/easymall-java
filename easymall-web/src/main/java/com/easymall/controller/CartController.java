package com.easymall.controller;

import com.easymall.entity.po.Cart;
import com.easymall.entity.result.Result;
import com.easymall.service.CartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")  // 实际路径：/api/cart/xxx
@Slf4j
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping
    public Result<List<Cart>> getList(@RequestHeader(value = "userId", required = true) String userId) {
        List<Cart> list = cartService.getList(userId);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<String> add(@RequestBody Cart cart,
                              @RequestHeader(value = "userId", required = true) String userId) {
        cart.setQuantity(cart.getQuantity() == null ? 1 : cart.getQuantity());
        boolean success = cartService.add(cart, userId);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    @PutMapping("/{cartId}/quantity")
    public Result<String> updateQuantity(@PathVariable String cartId,
                                         @RequestParam Integer quantity,
                                         @RequestHeader(value = "userId", required = true) String userId) {
        boolean success = cartService.updateQuantity(cartId, quantity);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @PutMapping("/{cartId}/selected")
    public Result<String> updateSelected(@PathVariable String cartId,
                                         @RequestParam Integer selected,
                                         @RequestHeader(value = "userId", required = true) String userId) {
        boolean success = cartService.updateSelected(cartId, selected);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @PutMapping("/selected/batch")
    public Result<String> batchUpdateSelected(@RequestBody List<String> cartIds,
                                              @RequestParam Integer selected,
                                              @RequestHeader(value = "userId", required = true) String userId) {
        cartService.batchUpdateSelected(cartIds, selected);
        return Result.success("批量更新成功");
    }

    @DeleteMapping("/{cartId}")
    public Result<String> delete(@PathVariable String cartId,
                                 @RequestHeader(value = "userId", required = true) String userId) {
        boolean success = cartService.delete(cartId);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<String> cartIds,
                                      @RequestHeader(value = "userId", required = true) String userId) {
        cartService.batchDelete(cartIds);
        return Result.success("批量删除成功");
    }

    @DeleteMapping("/clear")
    public Result<String> clear(@RequestHeader(value = "userId", required = true) String userId) {
        cartService.clear(userId);
        return Result.success("清空成功");
    }
}
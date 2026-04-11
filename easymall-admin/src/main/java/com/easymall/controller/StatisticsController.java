package com.easymall.controller;

import com.easymall.entity.result.Result;
import com.easymall.mapper.OrderMapper;
import com.easymall.mapper.ProductMapper;
import com.easymall.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 获取首页统计数据
     * GET /admin/statistics/dashboard
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 商品总数
        Integer productCount = productMapper.countAll();
        stats.put("productCount", productCount != null ? productCount : 0);

        // 订单总数
        Integer orderCount = orderMapper.countAll();
        stats.put("orderCount", orderCount != null ? orderCount : 0);

        // 用户总数
        Integer userCount = userMapper.countAll();
        stats.put("userCount", userCount != null ? userCount : 0);

        // 销售额（已完成的订单）
        BigDecimal totalSales = orderMapper.sumTotalAmountByStatus(3);
        stats.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);

        // 待发货订单数
        Integer pendingShipCount = orderMapper.countByStatus(1);
        stats.put("pendingShipCount", pendingShipCount != null ? pendingShipCount : 0);

        // 待付款订单数
        Integer pendingPayCount = orderMapper.countByStatus(0);
        stats.put("pendingPayCount", pendingPayCount != null ? pendingPayCount : 0);

        return Result.success(stats);
    }
}
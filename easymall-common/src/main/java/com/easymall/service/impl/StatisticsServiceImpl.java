package com.easymall.service.impl;

import com.easymall.mapper.OrderMapper;
import com.easymall.mapper.ProductMapper;
import com.easymall.mapper.UserMapper;
import com.easymall.service.StatisticsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 总用户数
        Integer userCount = userMapper.countAll();
        data.put("userCount", userCount != null ? userCount : 0);

        // 总订单数
        Integer orderCount = orderMapper.countAll();
        data.put("orderCount", orderCount != null ? orderCount : 0);

        // 待发货订单数
        Integer pendingShipCount = orderMapper.countByStatus(1);
        data.put("pendingShipCount", pendingShipCount != null ? pendingShipCount : 0);

        // 总销售额
        BigDecimal totalSales = orderMapper.sumTotalAmountByStatus();
        data.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);

        // 待付款订单数
        Integer pendingPayCount = orderMapper.countByStatus(0);
        data.put("pendingPayCount", pendingPayCount != null ? pendingPayCount : 0);

        return data;
    }

    @Override
    public Map<String, Object> getTrendData() {
        Map<String, Object> data = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<BigDecimal> amounts = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        // 获取近7天数据
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.format(formatter);
            String startTime = date + " 00:00:00";
            String endTime = date + " 23:59:59";

            dates.add(dateStr);
            BigDecimal amount = orderMapper.sumAmountByDateRange(startTime, endTime);
            amounts.add(amount != null ? amount : BigDecimal.ZERO);
            Integer count = orderMapper.countByDateRange(startTime, endTime);
            orders.add(count != null ? count : 0);
        }

        data.put("dates", dates);
        data.put("amounts", amounts);
        data.put("orders", orders);

        return data;
    }

    @Override
    public Map<String, Object> getHotProducts() {
        Map<String, Object> data = new HashMap<>();

        // 获取销量前10的商品
        List<Map<String, Object>> hotProducts = productMapper.selectHotProducts(10);
        data.put("hotProducts", hotProducts != null ? hotProducts : List.of());

        return data;
    }
}
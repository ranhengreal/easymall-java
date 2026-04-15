package com.easymall.controller;

import com.easymall.entity.result.Result;
import com.easymall.service.StatisticsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
@Slf4j
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 获取看板核心数据
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        Map<String, Object> data = statisticsService.getDashboardData();
        return Result.success(data);
    }

    /**
     * 获取近7天销售趋势
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend() {
        Map<String, Object> data = statisticsService.getTrendData();
        return Result.success(data);
    }

    /**
     * 获取热销商品排行
     */
    @GetMapping("/hot-products")
    public Result<Map<String, Object>> getHotProducts() {
        Map<String, Object> data = statisticsService.getHotProducts();
        return Result.success(data);
    }
}
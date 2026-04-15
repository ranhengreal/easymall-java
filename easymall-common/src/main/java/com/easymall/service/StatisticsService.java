package com.easymall.service;

import java.util.Map;

public interface StatisticsService {

    /**
     * 获取看板核心数据
     */
    Map<String, Object> getDashboardData();

    /**
     * 获取近7天销售趋势
     */
    Map<String, Object> getTrendData();

    /**
     * 获取热销商品排行
     */
    Map<String, Object> getHotProducts();
}
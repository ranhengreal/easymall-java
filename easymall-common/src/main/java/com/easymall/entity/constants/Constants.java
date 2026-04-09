package com.easymall.entity.constants;

public class Constants {
    private static final String REDIS_KEY_PREFIX ="easymall:";
    public static  final String REDIS_KEY_CHECK_CODE = REDIS_KEY_PREFIX + "checkCode:";
    public static final String REDIS_KEY_TOKEN_INFO_ADMIN = REDIS_KEY_PREFIX + "token:admin:";
    // 分类相关
    public static final String REDIS_KEY_CATEGORY_TREE = REDIS_KEY_PREFIX + "category:tree";
    // 过期时间（秒）
    public static final Long REDIS_KEY_EXPIRE_MIN = 60L;
    public static final Long REDIS_KEY_EXPIRE_DAY = REDIS_KEY_EXPIRE_MIN * 60 * 24;
    public static final Long REDIS_KEY_EXPIRE_HOUR = REDIS_KEY_EXPIRE_MIN * 60; // 1小时

    // 分类业务常量
    public static final String CATEGORY_ROOT_PARENT_ID = "0";
    public static final Integer CATEGORY_DEFAULT_SORT = 0;
    public static final String CATEGORY_ID_PREFIX = "C";
    public static final Integer CATEGORY_MAX_DEPTH = 5;
    public static final Integer CATEGORY_MAX_SORT = 999;

    // 品牌相关
    public static final String REDIS_KEY_BRAND_LIST = REDIS_KEY_PREFIX + "brand:list";
    public static final String REDIS_KEY_BRAND = REDIS_KEY_PREFIX + "brand:";

    // 品牌 ID 前缀
    public static final String BRAND_ID_PREFIX = "B";

    // 品牌状态
    public static final Integer BRAND_STATUS_ENABLE = 1;
    public static final Integer BRAND_STATUS_DISABLE = 0;

    // 商品相关
    public static final String REDIS_KEY_PRODUCT_LIST = REDIS_KEY_PREFIX + "product:list";
    public static final String REDIS_KEY_PRODUCT = REDIS_KEY_PREFIX + "product:";

    // 订单相关
    public static final String REDIS_KEY_ORDER_LIST = REDIS_KEY_PREFIX + "order:list";
    public static final String REDIS_KEY_ORDER = REDIS_KEY_PREFIX + "order:";

    // 订单状态
    public static final Integer ORDER_STATUS_WAIT_PAY = 0;      // 待付款
    public static final Integer ORDER_STATUS_WAIT_SHIP = 1;     // 待发货
    public static final Integer ORDER_STATUS_WAIT_RECEIVE = 2;  // 待收货
    public static final Integer ORDER_STATUS_COMPLETED = 3;     // 已完成
    public static final Integer ORDER_STATUS_CANCELLED = 4;     // 已取消
    public static final Integer ORDER_STATUS_AFTER_SALE = 5;    // 售后中

    // 支付状态
    public static final Integer PAY_STATUS_UNPAID = 0;   // 未支付
    public static final Integer PAY_STATUS_PAID = 1;     // 已支付
    public static final Integer PAY_STATUS_REFUND = 2;   // 已退款

    // 支付方式
    public static final Integer PAY_TYPE_WECHAT = 1;    // 微信支付
    public static final Integer PAY_TYPE_ALIPAY = 2;    // 支付宝
    public static final Integer PAY_TYPE_BALANCE = 3;   // 余额支付
}

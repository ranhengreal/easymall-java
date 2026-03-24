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



}

package com.yongwang.common.constant;

/**
 * 系统常量
 */
public class Constants {

    private Constants() {
    }

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Token请求头
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 管理员Token前缀（Redis Key）
     */
    public static final String ADMIN_TOKEN_PREFIX = "admin:token:";

    /**
     * 用户Token前缀（Redis Key）
     */
    public static final String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 验证码前缀（Redis Key）
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    /**
     * 超级管理员角色编码
     */
    public static final String SUPER_ADMIN_ROLE = "SUPER_ADMIN";

    /**
     * 删除标记：未删除
     */
    public static final int NOT_DELETED = 0;

    /**
     * 删除标记：已删除
     */
    public static final int DELETED = 1;

    /**
     * 状态：禁用
     */
    public static final int STATUS_DISABLED = 0;

    /**
     * 状态：启用
     */
    public static final int STATUS_ENABLED = 1;
}

package com.yongwang.common.result;

import lombok.Getter;

/**
 * 响应状态码枚举
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    FAIL(400, "操作失败"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    PARAM_ERROR(422, "参数校验失败"),

    // 服务端错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 业务错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "用户名或密码错误"),
    USER_DISABLED(1003, "用户已被禁用"),
    USER_EXISTS(1004, "用户已存在"),

    TOKEN_INVALID(1101, "Token无效"),
    TOKEN_EXPIRED(1102, "Token已过期"),

    // 商品相关 2xxx
    GOODS_NOT_FOUND(2001, "商品不存在"),
    GOODS_OFF_SHELF(2002, "商品已下架"),
    GOODS_STOCK_NOT_ENOUGH(2003, "商品库存不足"),

    // 订单相关 3xxx
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常"),
    ORDER_ALREADY_PAID(3003, "订单已支付"),
    ORDER_ALREADY_CANCELLED(3004, "订单已取消"),

    // 购物车相关 4xxx
    CART_EMPTY(4001, "购物车为空"),
    CART_ITEM_NOT_FOUND(4002, "购物车商品不存在"),

    // 优惠券相关 5xxx
    COUPON_NOT_FOUND(5001, "优惠券不存在"),
    COUPON_EXPIRED(5002, "优惠券已过期"),
    COUPON_USED(5003, "优惠券已使用"),
    COUPON_NOT_AVAILABLE(5004, "优惠券不可用"),

    // 通用数据错误 6xxx
    DATA_NOT_FOUND(6001, "数据不存在");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

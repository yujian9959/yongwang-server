package com.yongwang.core.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车视图对象（包含商品信息）
 */
@Data
public class CartVO {

    /**
     * 购物车项UID
     */
    private String uid;

    /**
     * 商品UID
     */
    private String goodsUid;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品原价
     */
    private BigDecimal originalPrice;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 是否选中
     */
    private Boolean selected;

    /**
     * 商品库存
     */
    private Integer stock;

    /**
     * 商品状态：0下架 1上架
     */
    private Integer goodsStatus;
}

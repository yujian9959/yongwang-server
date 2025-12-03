package com.yongwang.core.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 楼层商品视图对象
 */
@Data
public class FloorGoodsVO {

    /**
     * 商品UID
     */
    private String uid;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 销售价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 是否热销
     */
    private Integer isHot;

    /**
     * 是否新品
     */
    private Integer isNew;

    // ========== 秒杀专属字段 ==========

    /**
     * 秒杀价格（仅秒杀商品有值）
     */
    private BigDecimal seckillPrice;

    /**
     * 秒杀库存
     */
    private Integer seckillStock;

    /**
     * 已售数量
     */
    private Integer soldCount;

    /**
     * 限购数量
     */
    private Integer limitCount;

    /**
     * 秒杀商品UID（用于下单）
     */
    private String seckillGoodsUid;
}

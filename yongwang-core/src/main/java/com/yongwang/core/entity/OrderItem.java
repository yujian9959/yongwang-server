package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 订单商品明细实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_order_item")
public class OrderItem extends BaseEntity {

    /**
     * 订单UID（关联yw_order表）
     */
    private String orderUid;

    /**
     * 商品SPU的UID（关联yw_goods_spu表）
     */
    private String spuUid;

    /**
     * 商品SKU的UID（关联yw_goods_sku表，可为空）
     */
    private String skuUid;

    /**
     * 商品名称（下单时快照）
     */
    private String goodsName;

    /**
     * 商品图片URL（下单时快照）
     */
    private String goodsImage;

    /**
     * 规格信息（如：500g/袋，下单时快照）
     */
    private String specInfo;

    /**
     * 商品单价（下单时快照）
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 小计金额（单价 × 数量）
     */
    private BigDecimal totalAmount;
}

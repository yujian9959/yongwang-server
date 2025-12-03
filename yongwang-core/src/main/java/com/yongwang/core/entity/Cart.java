package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 购物车实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_cart")
public class Cart extends BaseEntity {

    /**
     * 用户UID（关联yw_user表）
     */
    private String userUid;

    /**
     * 商品SPU的UID（关联yw_goods_spu表）
     */
    private String spuUid;

    /**
     * 商品SKU的UID（关联yw_goods_sku表，可为空表示无规格）
     */
    private String skuUid;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 是否选中：0-未选中 1-已选中
     */
    private Integer selected;
}

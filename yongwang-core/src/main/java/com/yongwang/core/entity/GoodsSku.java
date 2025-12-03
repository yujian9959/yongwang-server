package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品SKU实体（库存单位，具体规格的商品）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_goods_sku")
public class GoodsSku extends BaseEntity {

    /**
     * 所属SPU的UID（关联yw_goods_spu表）
     */
    private String spuUid;

    /**
     * SKU编码（商家自定义编码）
     */
    private String skuCode;

    /**
     * 规格名称（如：500g/袋、1kg/袋）
     */
    private String specName;

    /**
     * 规格值JSON（如：{"重量":"500g","包装":"袋装"}）
     */
    private String specValue;

    /**
     * SKU图片URL
     */
    private String image;

    /**
     * 销售价格
     */
    private BigDecimal price;

    /**
     * 原价/划线价
     */
    private BigDecimal originalPrice;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 重量（单位：kg）
     */
    private BigDecimal weight;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}

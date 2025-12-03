package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品SPU实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_goods_spu")
public class GoodsSpu extends BaseEntity {

    /**
     * SPU编码
     */
    private String spuCode;

    /**
     * 分类UID
     */
    private String categoryUid;

    /**
     * 品牌UID
     */
    private String brandUid;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 副标题
     */
    private String subtitle;

    /**
     * 主图URL
     */
    private String mainImage;

    /**
     * 商品图片(JSON数组)
     */
    private String images;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 销售价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 总库存
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 单位
     */
    private String unit;

    /**
     * 重量(kg)
     */
    private BigDecimal weight;

    /**
     * 农药/肥料登记证号
     */
    private String registrationNo;

    /**
     * 生产厂家
     */
    private String manufacturer;

    /**
     * 规格信息
     */
    private String specInfo;

    /**
     * 商品属性(JSON)
     */
    private String attrs;

    /**
     * 使用方法
     */
    private String usageDesc;

    /**
     * 注意事项
     */
    private String notice;

    /**
     * 商品详情(富文本)
     */
    private String detail;

    /**
     * 状态：0下架 1上架
     */
    private Integer status;

    /**
     * 是否热销
     */
    private Integer isHot;

    /**
     * 是否新品
     */
    private Integer isNew;

    /**
     * 是否推荐
     */
    private Integer isRecommend;

    /**
     * 排序
     */
    private Integer sort;
}

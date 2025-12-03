package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 秒杀商品实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_seckill_goods")
public class SeckillGoods extends BaseEntity {

    /**
     * 秒杀活动UID
     */
    private String activityUid;

    /**
     * 商品SPU UID
     */
    private String spuUid;

    /**
     * 秒杀价格
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
     * 每人限购数量
     */
    private Integer limitCount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;
}

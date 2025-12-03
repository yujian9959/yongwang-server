package com.yongwang.core.vo;

import lombok.Data;

import java.util.List;

/**
 * 楼层视图对象（包含商品列表）
 */
@Data
public class FloorVO {

    /**
     * 楼层UID
     */
    private String uid;

    /**
     * 楼层名称
     */
    private String name;

    /**
     * 楼层类型
     */
    private String type;

    /**
     * 楼层图标
     */
    private String icon;

    /**
     * 背景颜色
     */
    private String bgColor;

    /**
     * 是否显示"查看更多"
     */
    private Integer showMore;

    /**
     * "查看更多"跳转链接
     */
    private String moreLink;

    /**
     * 商品列表
     */
    private List<FloorGoodsVO> goodsList;

    /**
     * 秒杀活动信息（仅秒杀楼层有值）
     */
    private SeckillActivityVO seckillActivity;
}

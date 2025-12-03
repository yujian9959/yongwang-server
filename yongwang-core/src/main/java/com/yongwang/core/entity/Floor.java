package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 首页楼层配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_floor")
public class Floor extends BaseEntity {

    /**
     * 楼层名称
     */
    private String name;

    /**
     * 楼层类型：seckill秒杀/hot热卖/new新品/recommend推荐
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
     * 是否显示"查看更多"：0否 1是
     */
    private Integer showMore;

    /**
     * "查看更多"跳转链接
     */
    private String moreLink;

    /**
     * 展示商品数量
     */
    private Integer goodsCount;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;
}

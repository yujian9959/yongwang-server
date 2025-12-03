package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 轮播图/广告位实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_banner")
public class Banner extends BaseEntity {

    /**
     * 轮播图标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 链接类型：0-无链接 1-商品详情 2-分类页 3-外部链接
     */
    private Integer linkType;

    /**
     * 链接值（根据linkType不同含义不同：商品UID/分类UID/外部URL）
     */
    private String linkValue;

    /**
     * 排序值（数值越小越靠前）
     */
    private Integer sort;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;
}

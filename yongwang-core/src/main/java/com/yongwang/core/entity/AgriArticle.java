package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 农技文章实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_agri_article")
public class AgriArticle extends BaseEntity {

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要/简介
     */
    private String summary;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 文章内容（富文本HTML）
     */
    private String content;

    /**
     * 文章分类（如：病虫害防治、施肥技术、种植技巧）
     */
    private String category;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 排序值（数值越小越靠前）
     */
    private Integer sort;

    /**
     * 状态：0-草稿 1-已发布
     */
    private Integer status;
}

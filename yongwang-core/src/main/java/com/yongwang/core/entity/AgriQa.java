package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 农技问答实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_agri_qa")
public class AgriQa extends BaseEntity {

    /**
     * 提问用户UID（关联yw_user表）
     */
    private String userUid;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题内容/描述
     */
    private String content;

    /**
     * 问题图片（JSON数组格式，存储多张图片URL）
     */
    private String images;

    /**
     * 问题分类（如：病虫害、施肥、种植）
     */
    private String category;

    /**
     * 回答内容
     */
    private String answer;

    /**
     * 回答人UID（关联yw_admin表，专家回答）
     */
    private String answerBy;

    /**
     * 回答时间
     */
    private LocalDateTime answerTime;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 状态：0-待回答 1-已回答
     */
    private Integer status;
}

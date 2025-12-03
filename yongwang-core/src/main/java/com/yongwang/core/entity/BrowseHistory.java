package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 浏览记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_browse_history")
public class BrowseHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 商品UID
     */
    private String goodsUid;

    /**
     * 浏览时间
     */
    private LocalDateTime browseTime;
}

package com.yongwang.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户收藏实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("yw_user_favorite")
public class UserFavorite extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 商品UID
     */
    private String goodsUid;
}

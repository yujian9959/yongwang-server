package com.yongwang.security;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * JWT用户详情
 */
@Data
@AllArgsConstructor
public class JwtUserDetails {

    /**
     * 用户UID
     */
    private String uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 类型：admin/user
     */
    private String type;
}

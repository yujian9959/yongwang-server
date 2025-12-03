package com.yongwang.service.wechat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.mini")
public class WechatConfig {

    /**
     * 小程序AppID
     */
    private String appid;

    /**
     * 小程序AppSecret
     */
    private String secret;
}

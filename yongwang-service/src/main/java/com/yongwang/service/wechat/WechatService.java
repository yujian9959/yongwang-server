package com.yongwang.service.wechat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatService {

    private final WechatConfig wechatConfig;
    private final ObjectMapper objectMapper;

    /**
     * 微信登录凭证校验接口地址
     */
    private static final String CODE2SESSION_URL =
        "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 获取接口调用凭证 access_token
     */
    private static final String ACCESS_TOKEN_URL =
        "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    /**
     * 获取手机号接口（新版，使用code换取）
     */
    private static final String GET_PHONE_NUMBER_URL =
        "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=%s";

    /**
     * 微信登录会话信息
     */
    @Data
    public static class WxSession {
        private String openid;
        private String sessionKey;
        private String unionid;
    }

    /**
     * 通过code获取微信用户openid
     *
     * @param code 微信登录凭证
     * @return 微信会话信息
     */
    public WxSession code2Session(String code) {
        String appid = wechatConfig.getAppid();
        String secret = wechatConfig.getSecret();

        // 检查配置
        if (appid == null || appid.isEmpty() || "your-appid-here".equals(appid)) {
            log.warn("微信小程序AppID未配置，使用模拟模式");
            return mockSession(code);
        }

        // 检查是否为开发环境的模拟code（微信开发者工具生成的）
        if (code != null && code.contains("mock")) {
            log.warn("检测到模拟code，使用模拟模式: code={}", code);
            return mockSession(code);
        }

        String url = String.format(CODE2SESSION_URL, appid, secret, code);
        log.info("调用微信code2Session接口: code={}", code);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            log.info("微信code2Session响应: {}", response);

            JsonNode jsonNode = objectMapper.readTree(response);

            // 检查是否有错误
            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                int errcode = jsonNode.get("errcode").asInt();
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                log.error("微信登录失败: errcode={}, errmsg={}", errcode, errmsg);

                // 根据错误码返回不同的错误信息
                switch (errcode) {
                    case 40029:
                        throw new BusinessException(ResultCode.PARAM_ERROR, "登录凭证无效");
                    case 45011:
                        throw new BusinessException(ResultCode.PARAM_ERROR, "请求过于频繁，请稍后再试");
                    case 40226:
                        throw new BusinessException(ResultCode.PARAM_ERROR, "用户被限制登录");
                    default:
                        throw new BusinessException(ResultCode.PARAM_ERROR, "微信登录失败: " + errmsg);
                }
            }

            // 解析成功响应
            WxSession session = new WxSession();
            session.setOpenid(jsonNode.get("openid").asText());
            session.setSessionKey(jsonNode.get("session_key").asText());
            if (jsonNode.has("unionid")) {
                session.setUnionid(jsonNode.get("unionid").asText());
            }

            log.info("微信登录成功: openid={}", session.getOpenid());
            return session;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用微信接口异常", e);
            // 接口调用失败时使用模拟模式
            log.warn("微信接口调用失败，使用模拟模式");
            return mockSession(code);
        }
    }

    /**
     * 模拟微信登录（开发环境或配置未完成时使用）
     */
    private WxSession mockSession(String code) {
        WxSession session = new WxSession();
        // 使用code生成一个稳定的模拟openid，这样同一个code会得到同一个用户
        String mockOpenid = "mock_" + Math.abs(code.hashCode());
        session.setOpenid(mockOpenid);
        session.setSessionKey("mock_session_key");
        log.info("使用模拟登录: openid={}", mockOpenid);
        return session;
    }

    /**
     * 手机号信息
     */
    @Data
    public static class PhoneInfo {
        private String phoneNumber;
        private String purePhoneNumber;
        private String countryCode;
    }

    /**
     * 获取接口调用凭证 access_token
     */
    public String getAccessToken() {
        String appid = wechatConfig.getAppid();
        String secret = wechatConfig.getSecret();

        if (appid == null || appid.isEmpty() || "your-appid-here".equals(appid)) {
            log.warn("微信小程序AppID未配置");
            return null;
        }

        String url = String.format(ACCESS_TOKEN_URL, appid, secret);
        log.info("获取微信access_token");

        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            log.info("获取access_token响应: {}", response);

            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                log.error("获取access_token失败: {}", response);
                return null;
            }

            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("获取access_token异常", e);
            return null;
        }
    }

    /**
     * 通过code获取用户手机号（新版接口，2023年后推荐使用）
     *
     * @param phoneCode 手机号授权回调的code（注意：这个code和登录的code不同）
     * @return 手机号信息
     */
    public PhoneInfo getPhoneNumber(String phoneCode) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            log.warn("获取access_token失败，使用模拟手机号");
            return mockPhoneInfo();
        }

        String url = String.format(GET_PHONE_NUMBER_URL, accessToken);
        log.info("调用微信获取手机号接口: phoneCode={}", phoneCode);

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 构建请求体
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = new HashMap<>();
            body.put("code", phoneCode);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(url, request, String.class);
            log.info("获取手机号响应: {}", response);

            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
                int errcode = jsonNode.get("errcode").asInt();
                String errmsg = jsonNode.has("errmsg") ? jsonNode.get("errmsg").asText() : "未知错误";
                log.error("获取手机号失败: errcode={}, errmsg={}", errcode, errmsg);
                throw new BusinessException(ResultCode.PARAM_ERROR, "获取手机号失败: " + errmsg);
            }

            // 解析手机号信息
            JsonNode phoneInfo = jsonNode.get("phone_info");
            PhoneInfo result = new PhoneInfo();
            result.setPhoneNumber(phoneInfo.get("phoneNumber").asText());
            result.setPurePhoneNumber(phoneInfo.get("purePhoneNumber").asText());
            result.setCountryCode(phoneInfo.get("countryCode").asText());

            log.info("获取手机号成功: {}", result.getPhoneNumber());
            return result;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取手机号异常", e);
            throw new BusinessException(ResultCode.PARAM_ERROR, "获取手机号失败");
        }
    }

    /**
     * 模拟手机号（开发环境使用）
     */
    private PhoneInfo mockPhoneInfo() {
        PhoneInfo info = new PhoneInfo();
        info.setPhoneNumber("13800138000");
        info.setPurePhoneNumber("13800138000");
        info.setCountryCode("86");
        log.info("使用模拟手机号: {}", info.getPhoneNumber());
        return info;
    }
}

package com.yongwang.controller.mini;

import com.yongwang.common.result.Result;
import com.yongwang.core.entity.User;
import com.yongwang.security.JwtUtils;
import com.yongwang.service.user.UserService;
import com.yongwang.service.wechat.WechatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序用户认证接口
 */
@Slf4j
@Tag(name = "小程序-用户认证")
@RestController
@RequestMapping("/mini/auth")
@RequiredArgsConstructor
public class MiniAuthController {

    private final UserService userService;
    private final WechatService wechatService;
    private final JwtUtils jwtUtils;

    @Data
    public static class LoginRequest {
        /**
         * 微信登录凭证code
         */
        private String code;
        /**
         * 用户昵称（可选，用于新用户注册）
         */
        private String nickname;
        /**
         * 用户头像（可选，用于新用户注册）
         */
        private String avatar;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private UserInfo userInfo;
        private Boolean isNewUser;
    }

    @Data
    public static class UserInfo {
        private String uid;
        private String nickname;
        private String avatar;
        private String phone;
        private Integer level;
        private Integer points;
    }

    /**
     * 构建用户信息响应对象
     */
    private UserInfo buildUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUid(user.getUid());
        userInfo.setNickname(user.getNickname() != null ? user.getNickname() : "微信用户");
        userInfo.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
        userInfo.setPhone(user.getPhone());
        userInfo.setLevel(user.getLevel() != null ? user.getLevel() : 0);
        userInfo.setPoints(user.getPoints() != null ? user.getPoints() : 0);
        return userInfo;
    }

    /**
     * 构建登录响应对象
     */
    private LoginResponse buildLoginResponse(User user, boolean isNewUser) {
        String token = jwtUtils.generateToken(user.getUid(), user.getNickname(), "user");

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setIsNewUser(isNewUser);
        response.setUserInfo(buildUserInfo(user));

        return response;
    }

    @Operation(summary = "微信登录", description = "通过微信code登录，用户不存在则自动注册")
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("微信登录请求: code={}", request.getCode());

        // 1. 通过code获取微信openid和session_key
        WechatService.WxSession wxSession = wechatService.code2Session(request.getCode());
        String openid = wxSession.getOpenid();
        String unionid = wxSession.getUnionid();
        String sessionKey = wxSession.getSessionKey();

        // 2. 根据openid查找或创建用户
        User user = userService.getByOpenid(openid);
        boolean isNewUser = false;

        if (user == null) {
            // 新用户注册
            log.info("新用户注册: openid={}", openid);
            user = userService.registerByWechat(
                openid,
                unionid,
                request.getNickname(),
                request.getAvatar()
            );
            // 存储 session_key
            userService.updateSessionKey(user.getUid(), sessionKey);
            isNewUser = true;
        } else {
            // 老用户登录，更新登录时间和 session_key
            log.info("用户登录: uid={}, openid={}", user.getUid(), openid);
            userService.updateLoginInfo(user.getUid(), sessionKey);

            // 如果传了昵称和头像，更新用户信息
            if (request.getNickname() != null || request.getAvatar() != null) {
                userService.updateWechatInfo(user.getUid(), request.getNickname(), request.getAvatar());
                // 重新获取更新后的用户信息
                user = userService.getByUid(user.getUid());
            }
        }

        // 3. 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.fail("账号已被禁用，请联系客服");
        }

        // 4. 构建响应
        log.info("登录成功: uid={}, isNewUser={}", user.getUid(), isNewUser);
        return Result.success(buildLoginResponse(user, isNewUser));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserInfo> info(@RequestAttribute("uid") String uid) {
        User user = userService.getByUid(uid);
        if (user == null) {
            return Result.fail("用户不存在");
        }

        return Result.success(buildUserInfo(user));
    }

    /**
     * 手机号登录请求
     */
    @Data
    public static class PhoneLoginRequest {
        /**
         * 手机号授权回调的code
         */
        private String phoneCode;
        /**
         * 微信登录凭证code
         */
        private String loginCode;
        /**
         * 用户昵称（可选）
         */
        private String nickname;
        /**
         * 用户头像（可选）
         */
        private String avatar;
    }

    @Operation(summary = "手机号快捷登录", description = "通过微信手机号授权登录，用户不存在则自动注册")
    @PostMapping("/login-phone")
    public Result<LoginResponse> loginByPhone(@RequestBody PhoneLoginRequest request) {
        log.info("手机号登录请求: phoneCode={}, loginCode={}", request.getPhoneCode(), request.getLoginCode());

        // 1. 通过loginCode获取微信openid和session_key
        WechatService.WxSession wxSession = wechatService.code2Session(request.getLoginCode());
        String openid = wxSession.getOpenid();
        String unionid = wxSession.getUnionid();
        String sessionKey = wxSession.getSessionKey();

        // 2. 通过phoneCode获取手机号
        WechatService.PhoneInfo phoneInfo = wechatService.getPhoneNumber(request.getPhoneCode());
        String phone = phoneInfo.getPurePhoneNumber();

        log.info("获取到手机号: {}", phone);

        // 3. 根据openid查找或创建用户
        User user = userService.getByOpenid(openid);
        boolean isNewUser = false;

        if (user == null) {
            // 新用户注册（带手机号）
            log.info("新用户注册: openid={}, phone={}", openid, phone);
            user = userService.registerByWechat(
                openid,
                unionid,
                request.getNickname(),
                request.getAvatar()
            );
            // 更新手机号和 session_key
            userService.updatePhone(user.getUid(), phone);
            userService.updateSessionKey(user.getUid(), sessionKey);
            user.setPhone(phone);
            isNewUser = true;
        } else {
            // 老用户登录，更新登录时间和 session_key
            log.info("用户登录: uid={}, openid={}", user.getUid(), openid);
            userService.updateLoginInfo(user.getUid(), sessionKey);

            // 更新手机号（支持换号场景：如果当前手机号与数据库中的不一致，则更新）
            if (!phone.equals(user.getPhone())) {
                log.info("检测到手机号变更: 旧手机号={}, 新手机号={}", user.getPhone(), phone);
                userService.updatePhone(user.getUid(), phone);
                user.setPhone(phone);
            }

            // 如果传了昵称和头像，更新用户信息
            if (request.getNickname() != null || request.getAvatar() != null) {
                userService.updateWechatInfo(user.getUid(), request.getNickname(), request.getAvatar());
                user = userService.getByUid(user.getUid());
            }
        }

        // 4. 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.fail("账号已被禁用，请联系客服");
        }

        // 5. 构建响应
        log.info("手机号登录成功: uid={}, phone={}, isNewUser={}", user.getUid(), phone, isNewUser);
        return Result.success(buildLoginResponse(user, isNewUser));
    }
}

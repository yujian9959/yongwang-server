package com.yongwang.service.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.User;
import com.yongwang.core.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 用户服务（小程序用户）
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    /**
     * 根据UID查询用户
     */
    public User getByUid(String uid) {
        return lambdaQuery()
                .eq(User::getUid, uid)
                .one();
    }

    /**
     * 根据OpenID查询用户
     */
    public User getByOpenid(String openid) {
        return lambdaQuery()
                .eq(User::getOpenid, openid)
                .one();
    }

    /**
     * 根据手机号查询用户
     */
    public User getByPhone(String phone) {
        return lambdaQuery()
                .eq(User::getPhone, phone)
                .one();
    }

    /**
     * 分页查询用户
     */
    public Page<User> page(int current, int size, String keyword, Integer status) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getNickname, keyword)
                    .or()
                    .like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 微信登录注册新用户
     */
    public User registerByWechat(String openid, String unionid, String nickname, String avatar) {
        User user = new User();
        user.setUid(UidGenerator.generate());
        user.setOpenid(openid);
        user.setUnionid(unionid);
        user.setNickname(nickname != null ? nickname : "微信用户");
        user.setAvatar(avatar);
        user.setStatus(1);
        user.setLevel(0);
        user.setPoints(0);
        user.setOrderCount(0);
        user.setRegisterTime(LocalDateTime.now());
        user.setLastLoginTime(LocalDateTime.now());
        save(user);
        return user;
    }

    /**
     * 更新用户最后登录时间
     */
    public void updateLoginTime(String uid) {
        lambdaUpdate()
                .eq(User::getUid, uid)
                .set(User::getLastLoginTime, LocalDateTime.now())
                .update();
    }

    /**
     * 更新微信用户信息（昵称、头像）
     */
    public void updateWechatInfo(String uid, String nickname, String avatar) {
        var updateWrapper = lambdaUpdate().eq(User::getUid, uid);
        if (nickname != null && !nickname.isEmpty()) {
            updateWrapper.set(User::getNickname, nickname);
        }
        if (avatar != null && !avatar.isEmpty()) {
            updateWrapper.set(User::getAvatar, avatar);
        }
        updateWrapper.update();
    }

    /**
     * 更新用户手机号
     */
    public void updatePhone(String uid, String phone) {
        lambdaUpdate()
                .eq(User::getUid, uid)
                .set(User::getPhone, phone)
                .update();
    }

    /**
     * 更新微信会话密钥
     */
    public void updateSessionKey(String uid, String sessionKey) {
        lambdaUpdate()
                .eq(User::getUid, uid)
                .set(User::getSessionKey, sessionKey)
                .update();
    }

    /**
     * 更新用户登录信息（登录时间和会话密钥）
     */
    public void updateLoginInfo(String uid, String sessionKey) {
        lambdaUpdate()
                .eq(User::getUid, uid)
                .set(User::getLastLoginTime, LocalDateTime.now())
                .set(User::getSessionKey, sessionKey)
                .update();
    }

    /**
     * 更新用户信息
     */
    public void updateUser(User user) {
        User existing = getByUid(user.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setId(existing.getId());
        updateById(user);
    }

    /**
     * 更新用户状态
     */
    public void updateStatus(String uid, Integer status) {
        User user = getByUid(uid);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(User::getId, user.getId())
                .set(User::getStatus, status)
                .update();
    }

    /**
     * 调整用户积分
     */
    public void adjustPoints(String uid, Integer points) {
        User user = getByUid(uid);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        int newPoints = user.getPoints() + points;
        if (newPoints < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "积分不足");
        }
        lambdaUpdate()
                .eq(User::getId, user.getId())
                .set(User::getPoints, newPoints)
                .update();
    }

    /**
     * 统计今日新增用户数
     */
    public long countTodayNew() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        return lambdaQuery()
                .ge(User::getCreateTime, todayStart)
                .count();
    }
}

package com.yongwang.service.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Admin;
import com.yongwang.core.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 管理员服务
 */
@Service
@RequiredArgsConstructor
public class AdminService extends ServiceImpl<AdminMapper, Admin> {

    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户名查询管理员
     */
    public Admin getByUsername(String username) {
        return lambdaQuery()
                .eq(Admin::getUsername, username)
                .one();
    }

    /**
     * 根据UID查询管理员
     */
    public Admin getByUid(String uid) {
        return lambdaQuery()
                .eq(Admin::getUid, uid)
                .one();
    }

    /**
     * 分页查询管理员
     */
    public Page<Admin> page(int current, int size, String keyword) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Admin::getUsername, keyword)
                    .or()
                    .like(Admin::getRealName, keyword);
        }
        wrapper.orderByDesc(Admin::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 创建管理员
     */
    public Admin create(Admin admin) {
        // 检查用户名是否存在
        if (getByUsername(admin.getUsername()) != null) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        admin.setUid(UidGenerator.generate());
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setStatus(1);
        save(admin);
        return admin;
    }

    /**
     * 更新管理员
     */
    public void updateAdmin(Admin admin) {
        Admin existing = getByUid(admin.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        // 如果修改了密码，需要加密
        if (StringUtils.hasText(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        } else {
            admin.setPassword(null); // 不更新密码
        }
        admin.setId(existing.getId());
        updateById(admin);
    }

    /**
     * 更新最后登录信息
     */
    public void updateLoginInfo(String uid, String ip) {
        lambdaUpdate()
                .eq(Admin::getUid, uid)
                .set(Admin::getLastLoginTime, LocalDateTime.now())
                .set(Admin::getLastLoginIp, ip)
                .update();
    }

    /**
     * 删除管理员
     */
    public void deleteByUid(String uid) {
        Admin admin = getByUid(uid);
        if (admin == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        removeById(admin.getId());
    }
}

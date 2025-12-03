package com.yongwang.service.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Role;
import com.yongwang.core.mapper.RoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 角色服务
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    /**
     * 根据UID查询角色
     */
    public Role getByUid(String uid) {
        return lambdaQuery()
                .eq(Role::getUid, uid)
                .one();
    }

    /**
     * 根据编码查询角色
     */
    public Role getByCode(String code) {
        return lambdaQuery()
                .eq(Role::getCode, code)
                .one();
    }

    /**
     * 分页查询角色
     */
    public Page<Role> page(int current, int size, String keyword) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Role::getName, keyword)
                    .or()
                    .like(Role::getCode, keyword);
        }
        wrapper.orderByAsc(Role::getSort);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 查询所有启用的角色
     */
    public List<Role> listEnabled() {
        return lambdaQuery()
                .eq(Role::getStatus, 1)
                .orderByAsc(Role::getSort)
                .list();
    }

    /**
     * 创建角色
     */
    public Role create(Role role) {
        // 检查编码是否存在
        if (getByCode(role.getCode()) != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "角色编码已存在");
        }
        role.setUid(UidGenerator.generate());
        role.setStatus(1);
        save(role);
        return role;
    }

    /**
     * 更新角色
     */
    public void updateRole(Role role) {
        Role existing = getByUid(role.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        role.setId(existing.getId());
        updateById(role);
    }

    /**
     * 删除角色
     */
    public void deleteByUid(String uid) {
        Role role = getByUid(uid);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(role.getId());
    }
}

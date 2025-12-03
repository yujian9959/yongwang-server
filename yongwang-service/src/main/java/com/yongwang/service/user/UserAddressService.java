package com.yongwang.service.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.UserAddress;
import com.yongwang.core.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户收货地址服务
 */
@Service
public class UserAddressService extends ServiceImpl<UserAddressMapper, UserAddress> {

    /**
     * 根据UID查询地址
     */
    public UserAddress getByUid(String uid) {
        return lambdaQuery()
                .eq(UserAddress::getUid, uid)
                .one();
    }

    /**
     * 查询用户的所有地址
     */
    public List<UserAddress> listByUserUid(String userUid) {
        return lambdaQuery()
                .eq(UserAddress::getUserUid, userUid)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime)
                .list();
    }

    /**
     * 获取用户默认地址
     */
    public UserAddress getDefaultByUserUid(String userUid) {
        return lambdaQuery()
                .eq(UserAddress::getUserUid, userUid)
                .eq(UserAddress::getIsDefault, 1)
                .one();
    }

    /**
     * 创建地址
     */
    @Transactional
    public UserAddress create(UserAddress address) {
        address.setUid(UidGenerator.generate());
        // 如果设为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            cancelDefault(address.getUserUid());
        }
        save(address);
        return address;
    }

    /**
     * 更新地址
     */
    @Transactional
    public void updateAddress(UserAddress address) {
        UserAddress existing = getByUid(address.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 如果设为默认地址，先取消其他默认地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            cancelDefault(existing.getUserUid());
        }
        address.setId(existing.getId());
        updateById(address);
    }

    /**
     * 设为默认地址
     */
    @Transactional
    public void setDefault(String uid) {
        UserAddress address = getByUid(uid);
        if (address == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        // 取消其他默认地址
        cancelDefault(address.getUserUid());
        // 设置当前地址为默认
        lambdaUpdate()
                .eq(UserAddress::getId, address.getId())
                .set(UserAddress::getIsDefault, 1)
                .update();
    }

    /**
     * 取消用户所有默认地址
     */
    private void cancelDefault(String userUid) {
        lambdaUpdate()
                .eq(UserAddress::getUserUid, userUid)
                .set(UserAddress::getIsDefault, 0)
                .update();
    }

    /**
     * 删除地址
     */
    public void deleteByUid(String uid) {
        UserAddress address = getByUid(uid);
        if (address == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(address.getId());
    }
}

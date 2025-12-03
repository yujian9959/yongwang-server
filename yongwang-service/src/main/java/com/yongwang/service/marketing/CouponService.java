package com.yongwang.service.marketing;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Coupon;
import com.yongwang.core.mapper.CouponMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券模板服务
 */
@Service
public class CouponService extends ServiceImpl<CouponMapper, Coupon> {

    /**
     * 根据UID查询优惠券
     */
    public Coupon getByUid(String uid) {
        return lambdaQuery()
                .eq(Coupon::getUid, uid)
                .one();
    }

    /**
     * 分页查询优惠券（后台管理）
     */
    public Page<Coupon> pageAdmin(int current, int size, String keyword, Integer status) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Coupon::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }
        wrapper.orderByDesc(Coupon::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 查询可领取的优惠券列表
     */
    public List<Coupon> listAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return lambdaQuery()
                .eq(Coupon::getStatus, 1)
                .le(Coupon::getStartTime, now)
                .ge(Coupon::getEndTime, now)
                .and(w -> w.eq(Coupon::getTotalCount, 0)
                        .or()
                        .apply("receive_count < total_count"))
                .orderByDesc(Coupon::getCreateTime)
                .list();
    }

    /**
     * 创建优惠券
     */
    public Coupon create(Coupon coupon) {
        coupon.setUid(UidGenerator.generate());
        coupon.setReceiveCount(0);
        coupon.setUseCount(0);
        coupon.setStatus(1);
        save(coupon);
        return coupon;
    }

    /**
     * 更新优惠券
     */
    public void updateCoupon(Coupon coupon) {
        Coupon existing = getByUid(coupon.getUid());
        if (existing == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        coupon.setId(existing.getId());
        updateById(coupon);
    }

    /**
     * 增加领取数量
     */
    public void incrementReceiveCount(String uid) {
        lambdaUpdate()
                .eq(Coupon::getUid, uid)
                .setSql("receive_count = receive_count + 1")
                .update();
    }

    /**
     * 增加使用数量
     */
    public void incrementUseCount(String uid) {
        lambdaUpdate()
                .eq(Coupon::getUid, uid)
                .setSql("use_count = use_count + 1")
                .update();
    }

    /**
     * 删除优惠券
     */
    public void deleteByUid(String uid) {
        Coupon coupon = getByUid(uid);
        if (coupon == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        removeById(coupon.getId());
    }
}

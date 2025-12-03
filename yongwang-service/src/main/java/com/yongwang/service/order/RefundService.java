package com.yongwang.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.Refund;
import com.yongwang.core.mapper.RefundMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后退款服务
 */
@Service
public class RefundService extends ServiceImpl<RefundMapper, Refund> {

    /**
     * 根据UID查询退款单
     */
    public Refund getByUid(String uid) {
        return lambdaQuery()
                .eq(Refund::getUid, uid)
                .one();
    }

    /**
     * 根据退款单号查询
     */
    public Refund getByRefundNo(String refundNo) {
        return lambdaQuery()
                .eq(Refund::getRefundNo, refundNo)
                .one();
    }

    /**
     * 分页查询退款单（后台管理）
     */
    public Page<Refund> pageAdmin(int current, int size, String keyword, String status) {
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Refund::getRefundNo, keyword)
                    .or()
                    .like(Refund::getOrderNo, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Refund::getStatus, status);
        }
        wrapper.orderByDesc(Refund::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 查询用户的退款单
     */
    public Page<Refund> pageByUserUid(String userUid, int current, int size) {
        LambdaQueryWrapper<Refund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Refund::getUserUid, userUid);
        wrapper.orderByDesc(Refund::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 申请退款
     */
    @Transactional
    public Refund apply(Refund refund) {
        refund.setUid(UidGenerator.generate());
        refund.setRefundNo(UidGenerator.generateRefundNo());
        refund.setStatus("pending");
        save(refund);
        return refund;
    }

    /**
     * 同意退款
     */
    @Transactional
    public void approve(String uid, BigDecimal actualAmount, String adminUid, String remark) {
        Refund refund = getByUid(uid);
        if (refund == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!"pending".equals(refund.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "退款单状态不正确");
        }
        lambdaUpdate()
                .eq(Refund::getId, refund.getId())
                .set(Refund::getStatus, "approved")
                .set(Refund::getActualAmount, actualAmount)
                .set(Refund::getHandleBy, adminUid)
                .set(Refund::getHandleTime, LocalDateTime.now())
                .set(Refund::getAdminRemark, remark)
                .update();
    }

    /**
     * 拒绝退款
     */
    @Transactional
    public void reject(String uid, String adminUid, String remark) {
        Refund refund = getByUid(uid);
        if (refund == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!"pending".equals(refund.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "退款单状态不正确");
        }
        lambdaUpdate()
                .eq(Refund::getId, refund.getId())
                .set(Refund::getStatus, "rejected")
                .set(Refund::getHandleBy, adminUid)
                .set(Refund::getHandleTime, LocalDateTime.now())
                .set(Refund::getAdminRemark, remark)
                .update();
    }

    /**
     * 完成退款
     */
    @Transactional
    public void complete(String uid) {
        Refund refund = getByUid(uid);
        if (refund == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND);
        }
        if (!"approved".equals(refund.getStatus())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "退款单状态不正确");
        }
        lambdaUpdate()
                .eq(Refund::getId, refund.getId())
                .set(Refund::getStatus, "completed")
                .set(Refund::getCompleteTime, LocalDateTime.now())
                .update();
    }
}

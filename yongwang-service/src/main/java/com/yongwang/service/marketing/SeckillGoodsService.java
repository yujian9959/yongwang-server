package com.yongwang.service.marketing;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.SeckillGoods;
import com.yongwang.core.mapper.SeckillGoodsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 秒杀商品服务
 */
@Service
public class SeckillGoodsService extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> {

    /**
     * 根据UID查询秒杀商品
     */
    public SeckillGoods getByUid(String uid) {
        return lambdaQuery()
                .eq(SeckillGoods::getUid, uid)
                .one();
    }

    /**
     * 根据活动UID查询秒杀商品列表
     */
    public List<SeckillGoods> listByActivityUid(String activityUid, int limit) {
        return lambdaQuery()
                .eq(SeckillGoods::getActivityUid, activityUid)
                .eq(SeckillGoods::getStatus, 1)
                .orderByAsc(SeckillGoods::getSort)
                .last("LIMIT " + limit)
                .list();
    }

    /**
     * 根据活动UID查询所有秒杀商品
     */
    public List<SeckillGoods> listByActivityUid(String activityUid) {
        return lambdaQuery()
                .eq(SeckillGoods::getActivityUid, activityUid)
                .eq(SeckillGoods::getStatus, 1)
                .orderByAsc(SeckillGoods::getSort)
                .list();
    }

    /**
     * 创建秒杀商品
     */
    public SeckillGoods create(SeckillGoods seckillGoods) {
        seckillGoods.setUid(UidGenerator.generate());
        if (seckillGoods.getSoldCount() == null) {
            seckillGoods.setSoldCount(0);
        }
        if (seckillGoods.getLimitCount() == null) {
            seckillGoods.setLimitCount(1);
        }
        if (seckillGoods.getStatus() == null) {
            seckillGoods.setStatus(1);
        }
        save(seckillGoods);
        return seckillGoods;
    }

    /**
     * 扣减秒杀库存
     */
    @Transactional(rollbackFor = Exception.class)
    public void reduceStock(String uid, int quantity) {
        SeckillGoods goods = getByUid(uid);
        if (goods == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "秒杀商品不存在");
        }
        int remainStock = goods.getSeckillStock() - goods.getSoldCount();
        if (remainStock < quantity) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "秒杀库存不足");
        }
        lambdaUpdate()
                .eq(SeckillGoods::getUid, uid)
                .set(SeckillGoods::getSoldCount, goods.getSoldCount() + quantity)
                .update();
    }

    /**
     * 恢复秒杀库存（取消订单时调用）
     */
    @Transactional(rollbackFor = Exception.class)
    public void restoreStock(String uid, int quantity) {
        SeckillGoods goods = getByUid(uid);
        if (goods == null) {
            return;
        }
        int newSoldCount = Math.max(0, goods.getSoldCount() - quantity);
        lambdaUpdate()
                .eq(SeckillGoods::getUid, uid)
                .set(SeckillGoods::getSoldCount, newSoldCount)
                .update();
    }
}

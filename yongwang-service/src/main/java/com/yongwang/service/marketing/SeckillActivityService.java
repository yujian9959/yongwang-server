package com.yongwang.service.marketing;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.SeckillActivity;
import com.yongwang.core.mapper.SeckillActivityMapper;
import com.yongwang.core.vo.SeckillActivityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 秒杀活动服务
 */
@Service
public class SeckillActivityService extends ServiceImpl<SeckillActivityMapper, SeckillActivity> {

    /**
     * 根据UID查询活动
     */
    public SeckillActivity getByUid(String uid) {
        return lambdaQuery()
                .eq(SeckillActivity::getUid, uid)
                .one();
    }

    /**
     * 获取当前进行中的秒杀活动
     */
    public SeckillActivity getCurrentActivity() {
        LocalDateTime now = LocalDateTime.now();
        return lambdaQuery()
                .le(SeckillActivity::getStartTime, now)
                .ge(SeckillActivity::getEndTime, now)
                .eq(SeckillActivity::getStatus, 1)
                .orderByDesc(SeckillActivity::getCreateTime)
                .last("LIMIT 1")
                .one();
    }

    /**
     * 获取当前秒杀活动VO（包含倒计时信息）
     */
    public SeckillActivityVO getCurrentActivityVO() {
        SeckillActivity activity = getCurrentActivity();
        if (activity == null) {
            // 如果没有进行中的活动，尝试获取即将开始的活动
            activity = getUpcomingActivity();
        }
        if (activity == null) {
            return null;
        }

        SeckillActivityVO vo = new SeckillActivityVO();
        BeanUtils.copyProperties(activity, vo);

        LocalDateTime now = LocalDateTime.now();
        vo.setServerTime(now);

        // 计算剩余秒数
        if (now.isBefore(activity.getStartTime())) {
            // 活动未开始，计算距离开始的时间
            vo.setStatus(0);
            vo.setRemainSeconds(Duration.between(now, activity.getStartTime()).getSeconds());
        } else if (now.isBefore(activity.getEndTime())) {
            // 活动进行中，计算距离结束的时间
            vo.setStatus(1);
            vo.setRemainSeconds(Duration.between(now, activity.getEndTime()).getSeconds());
        } else {
            // 活动已结束
            vo.setStatus(2);
            vo.setRemainSeconds(0L);
        }

        return vo;
    }

    /**
     * 获取即将开始的秒杀活动
     */
    public SeckillActivity getUpcomingActivity() {
        LocalDateTime now = LocalDateTime.now();
        return lambdaQuery()
                .gt(SeckillActivity::getStartTime, now)
                .orderByAsc(SeckillActivity::getStartTime)
                .last("LIMIT 1")
                .one();
    }

    /**
     * 创建秒杀活动
     */
    public SeckillActivity create(SeckillActivity activity) {
        activity.setUid(UidGenerator.generate());
        if (activity.getStatus() == null) {
            activity.setStatus(0); // 默认未开始
        }
        save(activity);
        return activity;
    }

    /**
     * 更新活动状态（定时任务调用）
     */
    public void updateActivityStatus() {
        LocalDateTime now = LocalDateTime.now();

        // 将已开始的活动状态更新为进行中
        lambdaUpdate()
                .le(SeckillActivity::getStartTime, now)
                .gt(SeckillActivity::getEndTime, now)
                .eq(SeckillActivity::getStatus, 0)
                .set(SeckillActivity::getStatus, 1)
                .update();

        // 将已结束的活动状态更新为已结束
        lambdaUpdate()
                .le(SeckillActivity::getEndTime, now)
                .ne(SeckillActivity::getStatus, 2)
                .set(SeckillActivity::getStatus, 2)
                .update();
    }
}

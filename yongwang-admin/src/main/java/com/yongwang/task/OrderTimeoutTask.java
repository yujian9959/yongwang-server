package com.yongwang.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yongwang.core.entity.Order;
import com.yongwang.core.entity.OrderItem;
import com.yongwang.core.mapper.OrderItemMapper;
import com.yongwang.core.mapper.OrderMapper;
import com.yongwang.service.goods.GoodsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时自动取消定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTimeoutTask {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final GoodsService goodsService;

    /**
     * 订单超时时间（分钟）
     */
    private static final int ORDER_TIMEOUT_MINUTES = 30;

    /**
     * 每分钟执行一次，检查超时未支付的订单
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelTimeoutOrders() {
        // 查询超时未支付的订单（创建时间超过30分钟且状态为pending）
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(ORDER_TIMEOUT_MINUTES);

        List<Order> timeoutOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, "pending")
                        .lt(Order::getCreateTime, timeoutTime)
        );

        if (timeoutOrders.isEmpty()) {
            return;
        }

        log.info("发现 {} 个超时未支付订单，开始自动取消", timeoutOrders.size());

        for (Order order : timeoutOrders) {
            try {
                cancelOrderAndRestoreStock(order);
                log.info("订单 {} 已自动取消", order.getOrderNo());
            } catch (Exception e) {
                log.error("取消订单 {} 失败: {}", order.getOrderNo(), e.getMessage());
            }
        }
    }

    /**
     * 取消订单并恢复库存
     */
    private void cancelOrderAndRestoreStock(Order order) {
        // 1. 更新订单状态为已取消
        order.setStatus("cancelled");
        order.setCancelReason("超时未支付，系统自动取消");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 2. 查询订单商品并恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderUid, order.getUid())
        );

        for (OrderItem item : items) {
            goodsService.restoreStock(item.getSpuUid(), item.getQuantity());
        }
    }
}

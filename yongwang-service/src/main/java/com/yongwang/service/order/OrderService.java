package com.yongwang.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.GoodsSpu;
import com.yongwang.core.entity.Order;
import com.yongwang.core.entity.OrderItem;
import com.yongwang.core.mapper.GoodsSpuMapper;
import com.yongwang.core.mapper.OrderItemMapper;
import com.yongwang.core.mapper.OrderMapper;
import com.yongwang.core.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务
 */
@Service
@RequiredArgsConstructor
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    private final OrderItemMapper orderItemMapper;
    private final GoodsSpuMapper goodsSpuMapper;

    /**
     * 根据UID查询订单
     */
    public Order getByUid(String uid) {
        return lambdaQuery()
                .eq(Order::getUid, uid)
                .one();
    }

    /**
     * 根据UID查询订单详情（包含商品列表）
     */
    public OrderVO getDetailByUid(String uid) {
        Order order = getByUid(uid);
        if (order == null) {
            return null;
        }
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 查询订单商品列表
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderUid, uid)
        );
        vo.setItems(items);
        return vo;
    }

    /**
     * 根据订单号查询订单
     */
    public Order getByOrderNo(String orderNo) {
        return lambdaQuery()
                .eq(Order::getOrderNo, orderNo)
                .one();
    }

    /**
     * 分页查询订单（后台管理）
     */
    public Page<Order> pageAdmin(int current, int size, String keyword, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Order::getOrderNo, keyword)
                    .or()
                    .like(Order::getReceiverName, keyword)
                    .or()
                    .like(Order::getReceiverPhone, keyword));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        return page(new Page<>(current, size), wrapper);
    }

    /**
     * 分页查询用户订单（包含商品明细）
     */
    public Page<OrderVO> pageByUserUid(String userUid, int current, int size, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserUid, userUid);
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = page(new Page<>(current, size), wrapper);

        // 转换为 OrderVO 并填充商品明细
        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = new java.util.ArrayList<>();
        for (Order order : orderPage.getRecords()) {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);
            // 查询订单商品列表
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderUid, order.getUid())
            );
            vo.setItems(items);
            voList.add(vo);
        }
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 创建订单
     */
    @Transactional
    public Order create(Order order) {
        order.setUid(UidGenerator.generate());
        order.setOrderNo(UidGenerator.generateOrderNo());
        order.setStatus("pending");
        save(order);
        return order;
    }

    /**
     * 支付订单（模拟）
     */
    @Transactional
    public void pay(String uid, String payTradeNo) {
        Order order = getByUid(uid);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        lambdaUpdate()
                .eq(Order::getId, order.getId())
                .set(Order::getStatus, "paid")
                .set(Order::getPayTime, LocalDateTime.now())
                .set(Order::getPayTradeNo, payTradeNo)
                .set(Order::getPayType, "wechat")
                .update();
    }

    /**
     * 发货
     */
    @Transactional
    public void ship(String uid, String expressCompany, String expressNo) {
        Order order = getByUid(uid);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        lambdaUpdate()
                .eq(Order::getId, order.getId())
                .set(Order::getStatus, "shipped")
                .set(Order::getExpressCompany, expressCompany)
                .set(Order::getExpressNo, expressNo)
                .set(Order::getShipTime, LocalDateTime.now())
                .update();
    }

    /**
     * 确认收货
     */
    @Transactional
    public void confirm(String uid) {
        Order order = getByUid(uid);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!"shipped".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        lambdaUpdate()
                .eq(Order::getId, order.getId())
                .set(Order::getStatus, "completed")
                .set(Order::getReceiveTime, LocalDateTime.now())
                .set(Order::getCompleteTime, LocalDateTime.now())
                .update();
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancel(String uid, String reason) {
        Order order = getByUid(uid);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!"pending".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "只有待付款订单可以取消");
        }

        // 1. 更新订单状态
        lambdaUpdate()
                .eq(Order::getId, order.getId())
                .set(Order::getStatus, "cancelled")
                .set(Order::getCancelReason, reason)
                .set(Order::getCancelTime, LocalDateTime.now())
                .update();

        // 2. 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderUid, uid)
        );
        for (OrderItem item : items) {
            restoreStock(item.getSpuUid(), item.getQuantity());
        }
    }

    /**
     * 恢复库存
     */
    private void restoreStock(String goodsUid, int quantity) {
        GoodsSpu goods = goodsSpuMapper.selectOne(
                new LambdaQueryWrapper<GoodsSpu>().eq(GoodsSpu::getUid, goodsUid)
        );
        if (goods != null) {
            int newSales = Math.max(0, goods.getSales() - quantity);
            goodsSpuMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<GoodsSpu>()
                            .eq(GoodsSpu::getUid, goodsUid)
                            .set(GoodsSpu::getStock, goods.getStock() + quantity)
                            .set(GoodsSpu::getSales, newSales)
            );
        }
    }

    /**
     * 更新商家备注
     */
    public void updateAdminRemark(String uid, String remark) {
        Order order = getByUid(uid);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        lambdaUpdate()
                .eq(Order::getId, order.getId())
                .set(Order::getAdminRemark, remark)
                .update();
    }

    /**
     * 统计今日订单数
     */
    public long countToday() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        return lambdaQuery()
                .ge(Order::getCreateTime, todayStart)
                .count();
    }

    /**
     * 按状态统计订单数
     */
    public long countByStatus(String status) {
        return lambdaQuery()
                .eq(Order::getStatus, status)
                .count();
    }

    /**
     * 按用户UID统计各状态订单数
     */
    public java.util.Map<String, Integer> countByUserUidAndStatus(String userUid) {
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        result.put("pending", lambdaQuery().eq(Order::getUserUid, userUid).eq(Order::getStatus, "pending").count().intValue());
        result.put("paid", lambdaQuery().eq(Order::getUserUid, userUid).eq(Order::getStatus, "paid").count().intValue());
        result.put("shipped", lambdaQuery().eq(Order::getUserUid, userUid).eq(Order::getStatus, "shipped").count().intValue());
        result.put("completed", lambdaQuery().eq(Order::getUserUid, userUid).eq(Order::getStatus, "completed").count().intValue());
        result.put("refund", 0); // 暂无退款状态
        return result;
    }

    /**
     * 统计总销售额
     */
    public java.math.BigDecimal sumTotalSales() {
        java.util.List<Order> orders = lambdaQuery()
                .eq(Order::getStatus, "completed")
                .list();
        return orders.stream()
                .map(Order::getPayAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 统计今日销售额
     */
    public java.math.BigDecimal sumTodaySales() {
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        java.util.List<Order> orders = lambdaQuery()
                .ge(Order::getPayTime, todayStart)
                .in(Order::getStatus, java.util.Arrays.asList("paid", "shipped", "completed"))
                .list();
        return orders.stream()
                .map(Order::getPayAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 按日期统计销售额
     */
    public java.math.BigDecimal sumSalesByDate(java.time.LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        java.util.List<Order> orders = lambdaQuery()
                .ge(Order::getPayTime, dayStart)
                .lt(Order::getPayTime, dayEnd)
                .in(Order::getStatus, java.util.Arrays.asList("paid", "shipped", "completed"))
                .list();
        return orders.stream()
                .map(Order::getPayAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    /**
     * 按日期统计订单数
     */
    public long countByDate(java.time.LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        return lambdaQuery()
                .ge(Order::getCreateTime, dayStart)
                .lt(Order::getCreateTime, dayEnd)
                .count();
    }

    /**
     * 获取销售趋势（近N天）
     */
    public java.util.Map<String, Object> getSalesTrend(int days) {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        java.util.List<String> dates = new java.util.ArrayList<>();
        java.util.List<java.math.BigDecimal> amounts = new java.util.ArrayList<>();
        java.util.List<Long> counts = new java.util.ArrayList<>();

        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            dates.add(date.format(formatter));

            java.util.List<Order> dayOrders = lambdaQuery()
                    .ge(Order::getPayTime, dayStart)
                    .lt(Order::getPayTime, dayEnd)
                    .in(Order::getStatus, java.util.Arrays.asList("paid", "shipped", "completed"))
                    .list();

            counts.add((long) dayOrders.size());
            java.math.BigDecimal dayAmount = dayOrders.stream()
                    .map(Order::getPayAmount)
                    .filter(java.util.Objects::nonNull)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            amounts.add(dayAmount);
        }

        result.put("dates", dates);
        result.put("amounts", amounts);
        result.put("counts", counts);
        return result;
    }
}

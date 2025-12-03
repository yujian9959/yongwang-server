package com.yongwang.controller.admin;

import com.yongwang.common.result.Result;
import com.yongwang.service.goods.GoodsService;
import com.yongwang.service.order.OrderService;
import com.yongwang.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 后台首页统计接口
 */
@Tag(name = "后台-首页统计")
@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final GoodsService goodsService;
    private final OrderService orderService;

    @Operation(summary = "获取统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("todayOrders", orderService.countToday());
        data.put("todaySales", orderService.sumTodaySales().toString());
        data.put("newUsers", userService.countTodayNew());
        data.put("totalGoods", goodsService.count());
        return Result.success(data);
    }

    @Operation(summary = "获取订单状态分布")
    @GetMapping("/order-status")
    public Result<List<Map<String, Object>>> orderStatus() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> pending = new HashMap<>();
        pending.put("name", "待付款");
        pending.put("value", orderService.countByStatus("pending"));
        list.add(pending);

        Map<String, Object> paid = new HashMap<>();
        paid.put("name", "待发货");
        paid.put("value", orderService.countByStatus("paid"));
        list.add(paid);

        Map<String, Object> completed = new HashMap<>();
        completed.put("name", "已完成");
        completed.put("value", orderService.countByStatus("completed"));
        list.add(completed);

        Map<String, Object> cancelled = new HashMap<>();
        cancelled.put("name", "已取消");
        cancelled.put("value", orderService.countByStatus("cancelled"));
        list.add(cancelled);

        return Result.success(list);
    }

    @Operation(summary = "获取销售趋势")
    @GetMapping("/sales-trend")
    public Result<Map<String, Object>> salesTrend(@RequestParam(defaultValue = "7") int days) {
        Map<String, Object> data = new HashMap<>();

        // 生成近N天的日期
        List<String> dates = new ArrayList<>();
        List<BigDecimal> sales = new ArrayList<>();
        List<Long> orders = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            // 获取每天的销售数据
            sales.add(orderService.sumSalesByDate(date));
            orders.add(orderService.countByDate(date));
        }

        data.put("dates", dates);
        data.put("sales", sales);
        data.put("orders", orders);

        return Result.success(data);
    }

    @Operation(summary = "获取热销商品")
    @GetMapping("/hot-goods")
    public Result<List<Map<String, Object>>> hotGoods(@RequestParam(defaultValue = "5") int limit) {
        List<Map<String, Object>> list = goodsService.getHotGoods(limit);
        return Result.success(list);
    }

    @Operation(summary = "获取待处理事项统计")
    @GetMapping("/todo-stats")
    public Result<Map<String, Object>> todoStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("pendingPayment", orderService.countByStatus("pending"));
        data.put("pendingShip", orderService.countByStatus("paid"));
        data.put("pendingRefund", 0L); // TODO: 实现退款统计
        data.put("lowStock", goodsService.countLowStock());
        data.put("pendingReview", 0L); // TODO: 实现待审核统计
        data.put("pendingQa", 0L); // TODO: 实现待回答问题统计
        return Result.success(data);
    }

    @Operation(summary = "获取统计概览（兼容旧接口）")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", userService.count());
        data.put("todayNewUsers", userService.countTodayNew());
        data.put("totalGoods", goodsService.count());
        data.put("onSaleGoods", goodsService.countOnSale());
        data.put("totalOrders", orderService.count());
        data.put("todayOrders", orderService.countToday());
        data.put("pendingOrders", orderService.countByStatus("pending"));
        data.put("shippedOrders", orderService.countByStatus("shipped"));
        data.put("totalSales", orderService.sumTotalSales());
        data.put("todaySales", orderService.sumTodaySales());
        return Result.success(data);
    }
}

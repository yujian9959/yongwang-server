package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Order;
import com.yongwang.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理接口
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Data
    public static class ShipRequest {
        private String expressCompany;
        private String expressNo;
    }

    @Data
    public static class RemarkRequest {
        private String remark;
    }

    @Operation(summary = "分页查询订单")
    @GetMapping("/list")
    public Result<Page<Order>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.success(orderService.pageAdmin(current, size, keyword, status));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{uid}")
    public Result<Order> detail(@PathVariable String uid) {
        return Result.success(orderService.getByUid(uid));
    }

    @Operation(summary = "发货")
    @PostMapping("/{uid}/ship")
    public Result<Void> ship(@PathVariable String uid, @RequestBody ShipRequest request) {
        orderService.ship(uid, request.getExpressCompany(), request.getExpressNo());
        return Result.success();
    }

    @Operation(summary = "更新商家备注")
    @PutMapping("/{uid}/remark")
    public Result<Void> updateRemark(@PathVariable String uid, @RequestBody RemarkRequest request) {
        orderService.updateAdminRemark(uid, request.getRemark());
        return Result.success();
    }
}

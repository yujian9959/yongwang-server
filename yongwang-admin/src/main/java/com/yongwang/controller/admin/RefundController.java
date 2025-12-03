package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Refund;
import com.yongwang.service.order.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 售后退款管理接口
 */
@Tag(name = "售后退款管理")
@RestController
@RequestMapping("/admin/refund")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @Data
    public static class ApproveRequest {
        private BigDecimal actualAmount;
        private String remark;
    }

    @Data
    public static class RejectRequest {
        private String remark;
    }

    @Operation(summary = "分页查询退款单")
    @GetMapping("/list")
    public Result<Page<Refund>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.success(refundService.pageAdmin(current, size, keyword, status));
    }

    @Operation(summary = "获取退款单详情")
    @GetMapping("/{uid}")
    public Result<Refund> detail(@PathVariable String uid) {
        return Result.success(refundService.getByUid(uid));
    }

    @Operation(summary = "同意退款")
    @PostMapping("/{uid}/approve")
    public Result<Void> approve(@PathVariable String uid,
                                @RequestBody ApproveRequest request,
                                @RequestAttribute("uid") String adminUid) {
        refundService.approve(uid, request.getActualAmount(), adminUid, request.getRemark());
        return Result.success();
    }

    @Operation(summary = "拒绝退款")
    @PostMapping("/{uid}/reject")
    public Result<Void> reject(@PathVariable String uid,
                               @RequestBody RejectRequest request,
                               @RequestAttribute("uid") String adminUid) {
        refundService.reject(uid, adminUid, request.getRemark());
        return Result.success();
    }

    @Operation(summary = "完成退款")
    @PostMapping("/{uid}/complete")
    public Result<Void> complete(@PathVariable String uid) {
        refundService.complete(uid);
        return Result.success();
    }
}

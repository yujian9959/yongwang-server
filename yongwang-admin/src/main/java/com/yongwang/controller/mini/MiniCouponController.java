package com.yongwang.controller.mini;

import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Coupon;
import com.yongwang.service.marketing.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序优惠券接口
 */
@Tag(name = "小程序-优惠券")
@RestController
@RequestMapping("/mini/coupon")
@RequiredArgsConstructor
public class MiniCouponController {

    private final CouponService couponService;

    @Operation(summary = "获取可领取的优惠券列表")
    @GetMapping("/available")
    public Result<List<Coupon>> available() {
        return Result.success(couponService.listAvailable());
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/{couponUid}/receive")
    public Result<Void> receive(@RequestAttribute("uid") String uid, @PathVariable String couponUid) {
        // TODO: 实现用户优惠券领取逻辑
        couponService.incrementReceiveCount(couponUid);
        return Result.success();
    }
}

package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Coupon;
import com.yongwang.service.marketing.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠券管理接口
 */
@Tag(name = "优惠券管理")
@RestController
@RequestMapping("/admin/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "分页查询优惠券")
    @GetMapping("/list")
    public Result<Page<Coupon>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(couponService.pageAdmin(current, size, keyword, status));
    }

    @Operation(summary = "获取优惠券详情")
    @GetMapping("/{uid}")
    public Result<Coupon> detail(@PathVariable String uid) {
        return Result.success(couponService.getByUid(uid));
    }

    @Operation(summary = "创建优惠券")
    @PostMapping
    public Result<Coupon> create(@RequestBody Coupon coupon) {
        return Result.success(couponService.create(coupon));
    }

    @Operation(summary = "更新优惠券")
    @PutMapping("/{uid}")
    public Result<Void> update(@PathVariable String uid, @RequestBody Coupon coupon) {
        coupon.setUid(uid);
        couponService.updateCoupon(coupon);
        return Result.success();
    }

    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        couponService.deleteByUid(uid);
        return Result.success();
    }
}

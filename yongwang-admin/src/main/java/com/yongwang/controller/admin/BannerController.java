package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Banner;
import com.yongwang.service.marketing.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 轮播图管理接口
 */
@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/admin/banner")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "分页查询轮播图")
    @GetMapping("/list")
    public Result<Page<Banner>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(bannerService.pageAdmin(current, size, keyword, status));
    }

    @Operation(summary = "获取轮播图详情")
    @GetMapping("/{uid}")
    public Result<Banner> detail(@PathVariable String uid) {
        return Result.success(bannerService.getByUid(uid));
    }

    @Operation(summary = "创建轮播图")
    @PostMapping
    public Result<Banner> create(@RequestBody Banner banner) {
        return Result.success(bannerService.create(banner));
    }

    @Operation(summary = "更新轮播图")
    @PutMapping("/{uid}")
    public Result<Void> update(@PathVariable String uid, @RequestBody Banner banner) {
        banner.setUid(uid);
        bannerService.updateBanner(banner);
        return Result.success();
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        bannerService.deleteByUid(uid);
        return Result.success();
    }
}

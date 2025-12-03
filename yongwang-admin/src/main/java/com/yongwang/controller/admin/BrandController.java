package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Brand;
import com.yongwang.service.goods.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 品牌管理接口
 */
@Tag(name = "品牌管理")
@RestController
@RequestMapping("/admin/brand")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "分页查询品牌")
    @GetMapping("/list")
    public Result<Page<Brand>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(brandService.page(current, size, keyword));
    }

    @Operation(summary = "获取所有启用的品牌")
    @GetMapping("/enabled")
    public Result<List<Brand>> listEnabled() {
        return Result.success(brandService.listEnabled());
    }

    @Operation(summary = "获取品牌详情")
    @GetMapping("/{uid}")
    public Result<Brand> detail(@PathVariable String uid) {
        return Result.success(brandService.getByUid(uid));
    }

    @Operation(summary = "创建品牌")
    @PostMapping
    public Result<Brand> create(@RequestBody Brand brand) {
        return Result.success(brandService.create(brand));
    }

    @Operation(summary = "更新品牌")
    @PutMapping("/{uid}")
    public Result<Void> update(@PathVariable String uid, @RequestBody Brand brand) {
        brand.setUid(uid);
        brandService.updateBrand(brand);
        return Result.success();
    }

    @Operation(summary = "删除品牌")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        brandService.deleteByUid(uid);
        return Result.success();
    }
}

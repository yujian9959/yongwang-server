package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Category;
import com.yongwang.service.goods.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理接口
 */
@Tag(name = "商品分类管理")
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "分页查询分类")
    @GetMapping("/list")
    public Result<Page<Category>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(categoryService.page(current, size, keyword));
    }

    @Operation(summary = "获取分类树")
    @GetMapping("/tree")
    public Result<List<Category>> tree() {
        return Result.success(categoryService.getTree());
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{uid}")
    public Result<Category> detail(@PathVariable String uid) {
        return Result.success(categoryService.getByUid(uid));
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        return Result.success(categoryService.create(category));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{uid}")
    public Result<Void> update(@PathVariable String uid, @RequestBody Category category) {
        category.setUid(uid);
        categoryService.updateCategory(category);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        categoryService.deleteByUid(uid);
        return Result.success();
    }
}

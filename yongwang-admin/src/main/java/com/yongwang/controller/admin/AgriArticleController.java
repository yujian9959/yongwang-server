package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.AgriArticle;
import com.yongwang.service.agri.AgriArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 农技文章管理接口
 */
@Tag(name = "后台-农技文章管理")
@RestController
@RequestMapping("/admin/agri-article")
@RequiredArgsConstructor
public class AgriArticleController {

    private final AgriArticleService agriArticleService;

    @Operation(summary = "分页查询文章")
    @GetMapping("/page")
    public Result<Page<AgriArticle>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryUid) {
        return Result.success(agriArticleService.page(current, size, keyword, categoryUid));
    }

    @Operation(summary = "获取文章详情")
    @GetMapping("/{uid}")
    public Result<AgriArticle> detail(@PathVariable String uid) {
        return Result.success(agriArticleService.getByUid(uid));
    }

    @Operation(summary = "创建文章")
    @PostMapping
    public Result<AgriArticle> create(@RequestBody AgriArticle article) {
        return Result.success(agriArticleService.create(article));
    }

    @Operation(summary = "更新文章")
    @PutMapping
    public Result<Void> update(@RequestBody AgriArticle article) {
        agriArticleService.updateArticle(article);
        return Result.success();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        agriArticleService.deleteByUid(uid);
        return Result.success();
    }

    @Operation(summary = "发布/下架文章")
    @PutMapping("/{uid}/status")
    public Result<Void> updateStatus(@PathVariable String uid, @RequestParam Integer status) {
        agriArticleService.updateStatus(uid, status);
        return Result.success();
    }
}

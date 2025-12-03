package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.AgriQa;
import com.yongwang.service.agri.AgriQaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 农技问答管理接口
 */
@Tag(name = "后台-农技问答管理")
@RestController
@RequestMapping("/admin/agri-qa")
@RequiredArgsConstructor
public class AgriQaController {

    private final AgriQaService agriQaService;

    @Operation(summary = "分页查询问答")
    @GetMapping("/page")
    public Result<Page<AgriQa>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(agriQaService.page(current, size, keyword, status));
    }

    @Operation(summary = "获取问答详情")
    @GetMapping("/{uid}")
    public Result<AgriQa> detail(@PathVariable String uid) {
        return Result.success(agriQaService.getByUid(uid));
    }

    @Operation(summary = "回复问答")
    @PutMapping("/{uid}/answer")
    public Result<Void> answer(@PathVariable String uid, @RequestBody String answer) {
        agriQaService.answer(uid, answer);
        return Result.success();
    }

    @Operation(summary = "删除问答")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        agriQaService.deleteByUid(uid);
        return Result.success();
    }

    @Operation(summary = "更新问答状态")
    @PutMapping("/{uid}/status")
    public Result<Void> updateStatus(@PathVariable String uid, @RequestParam Integer status) {
        agriQaService.updateStatus(uid, status);
        return Result.success();
    }
}

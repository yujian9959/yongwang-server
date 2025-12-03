package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Admin;
import com.yongwang.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员管理接口
 */
@Tag(name = "管理员管理")
@RestController
@RequestMapping("/admin/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "分页查询管理员")
    @GetMapping("/list")
    public Result<Page<Admin>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        Page<Admin> page = adminService.page(current, size, keyword);
        // 清除密码
        page.getRecords().forEach(admin -> admin.setPassword(null));
        return Result.success(page);
    }

    @Operation(summary = "获取管理员详情")
    @GetMapping("/{uid}")
    public Result<Admin> detail(@PathVariable String uid) {
        Admin admin = adminService.getByUid(uid);
        if (admin != null) {
            admin.setPassword(null);
        }
        return Result.success(admin);
    }

    @Operation(summary = "创建管理员")
    @PostMapping
    public Result<Admin> create(@RequestBody Admin admin) {
        Admin created = adminService.create(admin);
        created.setPassword(null);
        return Result.success(created);
    }

    @Operation(summary = "更新管理员")
    @PutMapping("/{uid}")
    public Result<Void> update(@PathVariable String uid, @RequestBody Admin admin) {
        admin.setUid(uid);
        adminService.updateAdmin(admin);
        return Result.success();
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        adminService.deleteByUid(uid);
        return Result.success();
    }
}

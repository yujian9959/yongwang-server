package com.yongwang.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Role;
import com.yongwang.service.admin.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "分页查询角色")
    @GetMapping("/list")
    public Result<Page<Role>> list(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(roleService.page(current, size, keyword));
    }

    @Operation(summary = "获取所有启用的角色")
    @GetMapping("/enabled")
    public Result<List<Role>> listEnabled() {
        return Result.success(roleService.listEnabled());
    }

    @Operation(summary = "获取角色详情")
    @GetMapping("/{uid}")
    public Result<Role> detail(@PathVariable String uid) {
        return Result.success(roleService.getByUid(uid));
    }

    @Operation(summary = "创建角色")
    @PostMapping
    public Result<Role> create(@RequestBody Role role) {
        return Result.success(roleService.create(role));
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{uid}")
    public Result<Void> update(@PathVariable String uid, @RequestBody Role role) {
        role.setUid(uid);
        roleService.updateRole(role);
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{uid}")
    public Result<Void> delete(@PathVariable String uid) {
        roleService.deleteByUid(uid);
        return Result.success();
    }
}

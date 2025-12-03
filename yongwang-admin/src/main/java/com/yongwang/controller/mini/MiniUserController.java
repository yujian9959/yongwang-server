package com.yongwang.controller.mini;

import com.yongwang.common.result.Result;
import com.yongwang.core.entity.User;
import com.yongwang.core.entity.UserAddress;
import com.yongwang.service.order.OrderService;
import com.yongwang.service.user.UserAddressService;
import com.yongwang.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 小程序用户接口
 */
@Tag(name = "小程序-用户中心")
@RestController
@RequestMapping("/mini/user")
@RequiredArgsConstructor
public class MiniUserController {

    private final UserService userService;
    private final UserAddressService userAddressService;
    private final OrderService orderService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<User> getInfo(@RequestAttribute("uid") String uid) {
        return Result.success(userService.getByUid(uid));
    }

    @Operation(summary = "获取订单统计")
    @GetMapping("/order-count")
    public Result<Map<String, Integer>> getOrderCount(@RequestAttribute("uid") String uid) {
        return Result.success(orderService.countByUserUidAndStatus(uid));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateInfo(@RequestAttribute("uid") String uid, @RequestBody User user) {
        user.setUid(uid);
        userService.updateUser(user);
        return Result.success();
    }

    // ========== 收货地址 ==========

    @Operation(summary = "获取地址列表")
    @GetMapping("/address/list")
    public Result<List<UserAddress>> addressList(@RequestAttribute("uid") String uid) {
        return Result.success(userAddressService.listByUserUid(uid));
    }

    @Operation(summary = "获取默认地址")
    @GetMapping("/address/default")
    public Result<UserAddress> defaultAddress(@RequestAttribute("uid") String uid) {
        return Result.success(userAddressService.getDefaultByUserUid(uid));
    }

    @Operation(summary = "获取地址详情")
    @GetMapping("/address/{addressUid}")
    public Result<UserAddress> addressDetail(@PathVariable String addressUid) {
        return Result.success(userAddressService.getByUid(addressUid));
    }

    @Operation(summary = "新增地址")
    @PostMapping("/address")
    public Result<UserAddress> createAddress(@RequestAttribute("uid") String uid, @RequestBody UserAddress address) {
        address.setUserUid(uid);
        return Result.success(userAddressService.create(address));
    }

    @Operation(summary = "更新地址")
    @PutMapping("/address/{addressUid}")
    public Result<Void> updateAddress(@PathVariable String addressUid, @RequestBody UserAddress address) {
        address.setUid(addressUid);
        userAddressService.updateAddress(address);
        return Result.success();
    }

    @Operation(summary = "设为默认地址")
    @PutMapping("/address/{addressUid}/default")
    public Result<Void> setDefaultAddress(@PathVariable String addressUid) {
        userAddressService.setDefault(addressUid);
        return Result.success();
    }

    @Operation(summary = "删除地址")
    @DeleteMapping("/address/{addressUid}")
    public Result<Void> deleteAddress(@PathVariable String addressUid) {
        userAddressService.deleteByUid(addressUid);
        return Result.success();
    }
}

package com.yongwang.controller.mini;

import com.yongwang.common.result.Result;
import com.yongwang.core.entity.Cart;
import com.yongwang.core.vo.CartVO;
import com.yongwang.service.cart.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序购物车接口
 */
@Tag(name = "小程序-购物车")
@RestController
@RequestMapping("/mini/cart")
@RequiredArgsConstructor
public class MiniCartController {

    private final CartService cartService;

    @Data
    public static class AddCartRequest {
        private String spuUid;
        private String skuUid;
        private Integer quantity;
    }

    @Data
    public static class UpdateQuantityRequest {
        private Integer quantity;
    }

    @Data
    public static class SelectAllRequest {
        private Integer selected;
    }

    @Data
    public static class BatchRemoveRequest {
        private List<String> uids;
    }

    @Operation(summary = "获取购物车列表")
    @GetMapping("/list")
    public Result<List<CartVO>> list(@RequestAttribute("uid") String uid) {
        return Result.success(cartService.listVOByUserUid(uid));
    }

    @Operation(summary = "获取购物车商品数量")
    @GetMapping("/count")
    public Result<Integer> count(@RequestAttribute("uid") String uid) {
        return Result.success(cartService.countByUserUid(uid));
    }

    @Operation(summary = "添加商品到购物车")
    @PostMapping("/add")
    public Result<Cart> add(@RequestAttribute("uid") String uid, @RequestBody AddCartRequest request) {
        Cart cart = cartService.addToCart(uid, request.getSpuUid(), request.getSkuUid(), request.getQuantity());
        return Result.success(cart);
    }

    @Operation(summary = "修改商品数量")
    @PutMapping("/{cartUid}/quantity")
    public Result<Void> updateQuantity(@PathVariable String cartUid, @RequestBody UpdateQuantityRequest request) {
        cartService.updateQuantity(cartUid, request.getQuantity());
        return Result.success();
    }

    @Operation(summary = "更新选中状态")
    @PutMapping("/{cartUid}/selected")
    public Result<Void> updateSelected(@PathVariable String cartUid, @RequestParam Integer selected) {
        cartService.updateSelected(cartUid, selected);
        return Result.success();
    }

    @Operation(summary = "全选/取消全选")
    @PutMapping("/select-all")
    public Result<Void> selectAll(@RequestAttribute("uid") String uid, @RequestBody SelectAllRequest request) {
        cartService.selectAll(uid, request.getSelected());
        return Result.success();
    }

    @Operation(summary = "删除购物车商品")
    @DeleteMapping("/{cartUid}")
    public Result<Void> delete(@PathVariable String cartUid) {
        cartService.deleteByUid(cartUid);
        return Result.success();
    }

    @Operation(summary = "批量删除购物车商品")
    @PostMapping("/batch-remove")
    public Result<Void> batchRemove(@RequestBody BatchRemoveRequest request) {
        cartService.batchDeleteByUids(request.getUids());
        return Result.success();
    }
}

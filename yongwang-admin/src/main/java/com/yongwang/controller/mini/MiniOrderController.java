package com.yongwang.controller.mini;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongwang.common.exception.BusinessException;
import com.yongwang.common.result.Result;
import com.yongwang.common.result.ResultCode;
import com.yongwang.common.utils.UidGenerator;
import com.yongwang.core.entity.*;
import com.yongwang.core.mapper.OrderItemMapper;
import com.yongwang.core.vo.CartVO;
import com.yongwang.service.cart.CartService;
import com.yongwang.service.goods.GoodsService;
import com.yongwang.service.order.OrderService;
import com.yongwang.service.user.UserAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * 小程序订单接口
 */
@Slf4j
@Tag(name = "小程序-订单")
@RestController
@RequestMapping("/mini/order")
@RequiredArgsConstructor
public class MiniOrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final GoodsService goodsService;
    private final UserAddressService userAddressService;
    private final OrderItemMapper orderItemMapper;

    @Data
    public static class CreateOrderRequest {
        private String addressUid;
        private List<String> cartUids;  // 购物车项UID列表
        private String goodsUid;         // 直接购买时的商品UID
        private Integer quantity;        // 直接购买时的数量
        private String couponUid;
        private String remark;
    }

    @Data
    public static class CancelRequest {
        private String reason;
    }

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, String>> create(@RequestAttribute("uid") String userUid, @RequestBody CreateOrderRequest request) {
        // 1. 获取收货地址
        UserAddress address = userAddressService.getByUid(request.getAddressUid());
        if (address == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "收货地址不存在");
        }

        // 2. 获取商品信息并计算金额
        List<CartVO> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (request.getCartUids() != null && !request.getCartUids().isEmpty()) {
            // 从购物车创建订单
            List<CartVO> cartList = cartService.listVOByUserUid(userUid);
            for (CartVO cart : cartList) {
                if (request.getCartUids().contains(cart.getUid())) {
                    orderItems.add(cart);
                    BigDecimal itemTotal = cart.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
                    totalAmount = totalAmount.add(itemTotal);
                }
            }
        } else if (request.getGoodsUid() != null) {
            // 直接购买
            GoodsSpu goods = goodsService.getByUid(request.getGoodsUid());
            if (goods == null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "商品不存在");
            }
            CartVO item = new CartVO();
            item.setGoodsUid(goods.getUid());
            item.setGoodsName(goods.getName());
            item.setGoodsImage(goods.getMainImage());
            item.setPrice(goods.getPrice());
            item.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
            item.setStock(goods.getStock());
            orderItems.add(item);
            totalAmount = goods.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        }

        if (orderItems.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请选择商品");
        }

        // 3. 校验库存并扣减
        for (CartVO item : orderItems) {
            GoodsSpu goods = goodsService.getByUid(item.getGoodsUid());
            if (goods == null) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "商品不存在：" + item.getGoodsName());
            }
            if (goods.getStock() < item.getQuantity()) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "商品库存不足：" + item.getGoodsName());
            }
            // 扣减库存
            goodsService.reduceStock(item.getGoodsUid(), item.getQuantity());
        }

        // 3. 创建订单
        Order order = new Order();
        order.setUserUid(userUid);
        order.setTotalAmount(totalAmount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO);
        order.setPointsAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayAmount(totalAmount);  // 实付金额 = 总金额 - 优惠
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());
        order.setRemark(request.getRemark());
        // 保存关联的购物车项UID（用于支付成功后删除）
        if (request.getCartUids() != null && !request.getCartUids().isEmpty()) {
            order.setCartUids(String.join(",", request.getCartUids()));
        }

        Order savedOrder = orderService.create(order);

        // 4. 保存订单商品明细
        for (CartVO item : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUid(UidGenerator.generate());
            orderItem.setOrderUid(savedOrder.getUid());
            orderItem.setSpuUid(item.getGoodsUid());
            orderItem.setGoodsName(item.getGoodsName());
            orderItem.setGoodsImage(item.getGoodsImage());
            orderItem.setSpecInfo("默认规格");
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalAmount(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderItemMapper.insert(orderItem);
        }

        // 5. 购物车项在支付成功后删除（不在此处删除，避免未支付时购物车数据丢失）

        // 6. 返回订单信息
        Map<String, String> result = new HashMap<>();
        result.put("orderUid", savedOrder.getUid());
        result.put("orderNo", savedOrder.getOrderNo());
        return Result.success(result);
    }

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<?> list(
            @RequestAttribute("uid") String uid,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        return Result.success(orderService.pageByUserUid(uid, current, size, status));
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{orderUid}")
    public Result<?> detail(@PathVariable String orderUid) {
        return Result.success(orderService.getDetailByUid(orderUid));
    }

    @Operation(summary = "模拟支付")
    @PostMapping("/{orderUid}/pay")
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> pay(@PathVariable String orderUid) {
        // 模拟支付成功
        String payTradeNo = "PAY" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        orderService.pay(orderUid, payTradeNo);

        // 支付成功后删除购物车中对应的商品
        Order order = orderService.getByUid(orderUid);
        if (order != null && order.getCartUids() != null && !order.getCartUids().isEmpty()) {
            String[] cartUidArray = order.getCartUids().split(",");
            for (String cartUid : cartUidArray) {
                try {
                    cartService.deleteByUid(cartUid.trim());
                } catch (Exception e) {
                    // 忽略删除失败（购物车项可能已被删除）
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("payTradeNo", payTradeNo);
        return Result.success(result);
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderUid}/cancel")
    public Result<Void> cancel(@PathVariable String orderUid, @RequestBody CancelRequest request) {
        orderService.cancel(orderUid, request.getReason());
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PostMapping("/{orderUid}/confirm")
    public Result<Void> confirm(@PathVariable String orderUid) {
        orderService.confirm(orderUid);
        return Result.success();
    }
}

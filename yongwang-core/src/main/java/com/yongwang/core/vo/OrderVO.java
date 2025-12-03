package com.yongwang.core.vo;

import com.yongwang.core.entity.Order;
import com.yongwang.core.entity.OrderItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 订单视图对象（包含订单商品列表）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderVO extends Order {

    /**
     * 订单商品列表
     */
    private List<OrderItem> items;
}

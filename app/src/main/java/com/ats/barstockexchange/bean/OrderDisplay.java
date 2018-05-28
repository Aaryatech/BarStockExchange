package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 15/11/17.
 */

public class OrderDisplay {

    private Order order;
    private List<OrderItem> orderItems;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "OrderDisplay{" +
                "order=" + order +
                ", orderItems=" + orderItems +
                '}';
    }
}

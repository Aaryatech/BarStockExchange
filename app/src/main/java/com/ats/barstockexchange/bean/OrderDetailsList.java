package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 30/11/17.
 */

public class OrderDetailsList {
    private Integer orderDetailsId;
    private Integer orderId;
    private Integer itemId;
    private Integer quantity;
    private float rate;
    private Integer status;
    private Integer isMixer;

    public OrderDetailsList(Integer orderDetailsId, Integer orderId, Integer itemId, Integer quantity, float rate, Integer status, Integer isMixer) {
        this.orderDetailsId = orderDetailsId;
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.rate = rate;
        this.status = status;
        this.isMixer = isMixer;
    }

    public Integer getOrderDetailsId() {
        return orderDetailsId;
    }

    public void setOrderDetailsId(Integer orderDetailsId) {
        this.orderDetailsId = orderDetailsId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsMixer() {
        return isMixer;
    }

    public void setIsMixer(Integer isMixer) {
        this.isMixer = isMixer;
    }

    @Override
    public String toString() {
        return "OrderDetailsList{" +
                "orderDetailsId=" + orderDetailsId +
                ", orderId=" + orderId +
                ", itemId=" + itemId +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", status=" + status +
                ", isMixer=" + isMixer +
                '}';
    }
}

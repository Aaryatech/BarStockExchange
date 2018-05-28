package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 15/11/17.
 */

public class Order {

    private Integer orderId;
    private Integer userId;
    private Integer tableNo;
    private Integer billStatus;
    private String orderDate;
    private Integer delStatus;

    public Order(Integer orderId, Integer tableNo, Integer billStatus, String orderDate) {
        this.orderId = orderId;
        this.tableNo = tableNo;
        this.billStatus = billStatus;
        this.orderDate = orderDate;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTableNo() {
        return tableNo;
    }

    public void setTableNo(Integer tableNo) {
        this.tableNo = tableNo;
    }

    public Integer getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", tableNo=" + tableNo +
                ", billStatus=" + billStatus +
                ", orderDate='" + orderDate + '\'' +
                ", delStatus=" + delStatus +
                '}';
    }
}

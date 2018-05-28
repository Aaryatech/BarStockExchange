package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 30/11/17.
 */

public class OrderEntry {

    private Integer orderId;
    private Integer userId;
    private Integer tableNo;
    private Integer billStatus;
    private String orderDate;
    private Integer delStatus;
    private List<OrderDetailsList> orderDetailsList;

    public OrderEntry(Integer orderId, Integer userId, Integer tableNo, Integer billStatus, String orderDate, Integer delStatus, List<OrderDetailsList> orderDetailsList) {
        this.orderId = orderId;
        this.userId = userId;
        this.tableNo = tableNo;
        this.billStatus = billStatus;
        this.orderDate = orderDate;
        this.delStatus = delStatus;
        this.orderDetailsList = orderDetailsList;
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

    public List<OrderDetailsList> getOrderDetailsList() {
        return orderDetailsList;
    }

    public void setOrderDetailsList(List<OrderDetailsList> orderDetailsList) {
        this.orderDetailsList = orderDetailsList;
    }

    @Override
    public String toString() {
        return "OrderEntry{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", tableNo=" + tableNo +
                ", billStatus=" + billStatus +
                ", orderDate='" + orderDate + '\'' +
                ", delStatus=" + delStatus +
                ", orderDetailsList=" + orderDetailsList +
                '}';
    }
}

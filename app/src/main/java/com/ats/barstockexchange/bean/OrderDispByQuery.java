package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 15/11/17.
 */

public class OrderDispByQuery {

    private Integer orderId;
    private Integer tableNo;
    private Integer billStatus;
    private String orderDate;
    private Integer itemId;
    private String itemName;
    private Integer qty;
    private float rate;
    private float sgst;
    private float cgst;


    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public float getSgst() {
        return sgst;
    }

    public void setSgst(float sgst) {
        this.sgst = sgst;
    }

    public float getCgst() {
        return cgst;
    }

    public void setCgst(float cgst) {
        this.cgst = cgst;
    }

    @Override
    public String toString() {
        return "OrderDispByQuery{" +
                "orderId=" + orderId +
                ", tableNo=" + tableNo +
                ", billStatus=" + billStatus +
                ", orderDate='" + orderDate + '\'' +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", qty=" + qty +
                ", rate=" + rate +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                '}';
    }
}

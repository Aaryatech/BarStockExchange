package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 15/11/17.
 */

public class OrderItem {

    private Integer orderId;
    private Integer itemId;
    private String itemName;
    private Integer quantity;
    private float rate;
    private float sgst;
    private float cgst;

    public OrderItem(Integer itemId, String itemName, Integer quantity, float rate, float sgst, float cgst) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.rate = rate;
        this.sgst = sgst;
        this.cgst = cgst;
    }

    public OrderItem(Integer itemId, String itemName, Integer quantity) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public OrderItem(Integer itemId, String itemName, Integer quantity, float rate) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.rate = rate;
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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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
        return "OrderItem{" +
                "orderId=" + orderId +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                '}';
    }
}

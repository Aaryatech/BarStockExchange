package com.ats.barstockexchange.bean;

/**
 * Created by MAXADMIN on 15/3/2018.
 */

public class BillDetailsList {

    private int billDetailsId;
    private int billId;
    private int orderId;
    private int delStatus;
    private int item_id;
    private String itemName;
    private int quantity;
    private float rate;
    private float sgst;
    private float cgst;
    private float total;
    private float taxableAmt;
    private float totalTax;

    public int getBillDetailsId() {
        return billDetailsId;
    }

    public void setBillDetailsId(int billDetailsId) {
        this.billDetailsId = billDetailsId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(int delStatus) {
        this.delStatus = delStatus;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
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

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTaxableAmt() {
        return taxableAmt;
    }

    public void setTaxableAmt(float taxableAmt) {
        this.taxableAmt = taxableAmt;
    }

    public float getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(float totalTax) {
        this.totalTax = totalTax;
    }

    @Override
    public String toString() {
        return "BillDetailsList{" +
                "billDetailsId=" + billDetailsId +
                ", billId=" + billId +
                ", orderId=" + orderId +
                ", delStatus=" + delStatus +
                ", item_id=" + item_id +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                ", total=" + total +
                ", taxableAmt=" + taxableAmt +
                ", totalTax=" + totalTax +
                '}';
    }
}

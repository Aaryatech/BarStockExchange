package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 2/12/17.
 */

public class CustomBillHeader {

    private int billId;
    private int userId;
    private String billDate;
    private float discount;
    private double payableAmount;
    private List<CustomBillItems> customBillItems;
    private int billNo;
    private int tableNo;

    public CustomBillHeader(int billId, int userId, String billDate, float discount, double payableAmount, List<CustomBillItems> customBillItems) {
        this.billId = billId;
        this.userId = userId;
        this.billDate = billDate;
        this.discount = discount;
        this.payableAmount = payableAmount;
        this.customBillItems = customBillItems;
    }

    public CustomBillHeader(int billId, int userId, String billDate, float discount, double payableAmount, List<CustomBillItems> customBillItems, int billNo) {
        this.billId = billId;
        this.userId = userId;
        this.billDate = billDate;
        this.discount = discount;
        this.payableAmount = payableAmount;
        this.customBillItems = customBillItems;
        this.billNo = billNo;
    }

    public CustomBillHeader() {
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public List<CustomBillItems> getCustomBillItems() {
        return customBillItems;
    }

    public void setCustomBillItems(List<CustomBillItems> customBillItems) {
        this.customBillItems = customBillItems;
    }

    public int getBillNo() {
        return billNo;
    }

    public void setBillNo(int billNo) {
        this.billNo = billNo;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    @Override
    public String toString() {
        return "CustomBillHeader{" +
                "billId=" + billId +
                ", userId=" + userId +
                ", billDate='" + billDate + '\'' +
                ", discount=" + discount +
                ", payableAmount=" + payableAmount +
                ", customBillItems=" + customBillItems +
                ", billNo=" + billNo +
                ", tableNo=" + tableNo +
                '}';
    }
}

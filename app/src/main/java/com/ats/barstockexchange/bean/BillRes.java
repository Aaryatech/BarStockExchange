package com.ats.barstockexchange.bean;

/**
 * Created by MAXADMIN on 15/3/2018.
 */

public class BillRes {

    private int billId;
    private String billDate;
    private int delStatus;
    private int userId;
    private int enterBy;
    private int billClose;
    private float discount;
    private float grandTotal;
    private float payableAmt;
    private int tableNo;
    private int billNo;

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public int getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(int delStatus) {
        this.delStatus = delStatus;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEnterBy() {
        return enterBy;
    }

    public void setEnterBy(int enterBy) {
        this.enterBy = enterBy;
    }

    public int getBillClose() {
        return billClose;
    }

    public void setBillClose(int billClose) {
        this.billClose = billClose;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(float grandTotal) {
        this.grandTotal = grandTotal;
    }

    public float getPayableAmt() {
        return payableAmt;
    }

    public void setPayableAmt(float payableAmt) {
        this.payableAmt = payableAmt;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public int getBillNo() {
        return billNo;
    }

    public void setBillNo(int billNo) {
        this.billNo = billNo;
    }

    @Override
    public String toString() {
        return "BillRes{" +
                "billId=" + billId +
                ", billDate='" + billDate + '\'' +
                ", delStatus=" + delStatus +
                ", userId=" + userId +
                ", enterBy=" + enterBy +
                ", billClose=" + billClose +
                ", discount=" + discount +
                ", grandTotal=" + grandTotal +
                ", payableAmt=" + payableAmt +
                ", tableNo=" + tableNo +
                ", billNo=" + billNo +
                '}';
    }
}

package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 2/12/17.
 */

public class Bill {

    private Integer billDetailsId;
    private Integer billId;
    private String billDate;
    private Integer delStatus;
    private Integer userId;
    private Integer enterBy;
    private Integer billClose;
    private float discount;
    private Double grandTotal;
    private Double payableAmt;
    private Integer tableNo;
    private Integer orderId;
    private Integer itemId;
    private String itemName;
    private Integer quantity;
    private float rate;
    private float sgst;
    private float cgst;
    private float total;
    private Double taxableAmt;
    private float totalTax;
    private int billNo;
    private int catId;

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public Integer getBillDetailsId() {
        return billDetailsId;
    }

    public void setBillDetailsId(Integer billDetailsId) {
        this.billDetailsId = billDetailsId;
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getEnterBy() {
        return enterBy;
    }

    public void setEnterBy(Integer enterBy) {
        this.enterBy = enterBy;
    }

    public Integer getBillClose() {
        return billClose;
    }

    public void setBillClose(Integer billClose) {
        this.billClose = billClose;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public Double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Double getPayableAmt() {
        return payableAmt;
    }

    public void setPayableAmt(Double payableAmt) {
        this.payableAmt = payableAmt;
    }

    public Integer getTableNo() {
        return tableNo;
    }

    public void setTableNo(Integer tableNo) {
        this.tableNo = tableNo;
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

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Double getTaxableAmt() {
        return taxableAmt;
    }

    public void setTaxableAmt(Double taxableAmt) {
        this.taxableAmt = taxableAmt;
    }

    public float getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(float totalTax) {
        this.totalTax = totalTax;
    }

    public int getBillNo() {
        return billNo;
    }

    public void setBillNo(int billNo) {
        this.billNo = billNo;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billDetailsId=" + billDetailsId +
                ", billId=" + billId +
                ", billDate='" + billDate + '\'' +
                ", delStatus=" + delStatus +
                ", userId=" + userId +
                ", enterBy=" + enterBy +
                ", billClose=" + billClose +
                ", discount=" + discount +
                ", grandTotal=" + grandTotal +
                ", payableAmt=" + payableAmt +
                ", tableNo=" + tableNo +
                ", orderId=" + orderId +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                ", total=" + total +
                ", taxableAmt=" + taxableAmt +
                ", totalTax=" + totalTax +
                ", billNo=" + billNo +
                '}';
    }
}

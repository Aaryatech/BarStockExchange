package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 2/12/17.
 */

public class CustomBillItems {

    private String itemName;
    private int quantity;
    private float rate;

    public CustomBillItems(String itemName, int quantity, float rate) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.rate = rate;
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

    @Override
    public String toString() {
        return "CustomBillItems{" +
                "itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", rate=" + rate +
                '}';
    }
}

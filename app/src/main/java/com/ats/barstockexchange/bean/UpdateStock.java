package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 13/11/17.
 */

public class UpdateStock {

    private int itemId;
    private int stock;
    private int UserId;

    public UpdateStock(int itemId, int stock, int userId) {
        this.itemId = itemId;
        this.stock = stock;
        UserId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int userId) {
        UserId = userId;
    }

    @Override
    public String toString() {
        return "UpdateStock{" +
                "itemId=" + itemId +
                ", stock=" + stock +
                ", UserId=" + UserId +
                '}';
    }
}

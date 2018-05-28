package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 14/11/17.
 */

public class UpdateRate {

    private int itemId;
    private float minRate;
    private float maxRate;
    private int userId;

    public UpdateRate(int itemId, float minRate, float maxRate, int userId) {
        this.itemId = itemId;
        this.minRate = minRate;
        this.maxRate = maxRate;
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public float getMinRate() {
        return minRate;
    }

    public void setMinRate(float minRate) {
        this.minRate = minRate;
    }

    public float getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(float maxRate) {
        this.maxRate = maxRate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UpdateRate{" +
                "itemId=" + itemId +
                ", minRate=" + minRate +
                ", maxRate=" + maxRate +
                ", userId=" + userId +
                '}';
    }
}

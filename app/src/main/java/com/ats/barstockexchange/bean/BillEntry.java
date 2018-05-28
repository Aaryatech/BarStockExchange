package com.ats.barstockexchange.bean;

import java.util.ArrayList;

/**
 * Created by maxadmin on 1/12/17.
 */

public class BillEntry {

    private int userId;
    private int enterBy;
    private float discount;
    private ArrayList<Integer> orderIdList;

    public BillEntry(int userId, int enterBy, float discount, ArrayList<Integer> orderIdList) {
        this.userId = userId;
        this.enterBy = enterBy;
        this.discount = discount;
        this.orderIdList = orderIdList;
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

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public ArrayList<Integer> getOrderIdList() {
        return orderIdList;
    }

    public void setOrderIdList(ArrayList<Integer> orderIdList) {
        this.orderIdList = orderIdList;
    }


    @Override
    public String toString() {
        return "BillEntry{" +
                "userId=" + userId +
                ", enterBy=" + enterBy +
                ", discount=" + discount +
                ", orderIdList=" + orderIdList +
                '}';
    }
}

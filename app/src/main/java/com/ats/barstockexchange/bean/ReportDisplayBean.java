package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by MAXADMIN on 12/3/2018.
 */

public class ReportDisplayBean {

    private int catId;
    private String catName;
    private List<Bill> billList;

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public List<Bill> getBillList() {
        return billList;
    }

    public void setBillList(List<Bill> billList) {
        this.billList = billList;
    }

    @Override
    public String toString() {
        return "ReportDisplayBean{" +
                "catId=" + catId +
                ", catName='" + catName + '\'' +
                ", billList=" + billList +
                '}';
    }
}

package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by MAXADMIN on 15/3/2018.
 */

public class GenerateBillOutput {

    private BillRes billRes;
    private List<BillDetailsList> billDetailsList;


    public BillRes getBillRes() {
        return billRes;
    }

    public void setBillRes(BillRes billRes) {
        this.billRes = billRes;
    }

    public List<BillDetailsList> getBillDetailsList() {
        return billDetailsList;
    }

    public void setBillDetailsList(List<BillDetailsList> billDetailsList) {
        this.billDetailsList = billDetailsList;
    }

    @Override
    public String toString() {
        return "GenerateBillOutput{" +
                "billRes=" + billRes +
                ", billDetailsList=" + billDetailsList +
                '}';
    }
}

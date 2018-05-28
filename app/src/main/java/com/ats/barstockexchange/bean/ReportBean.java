package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by MAXADMIN on 12/3/2018.
 */

public class ReportBean {

    private List<Bill> bill;
    private ErrorMessage errorMessage;

    public List<Bill> getBill() {
        return bill;
    }

    public void setBill(List<Bill> bill) {
        this.bill = bill;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ReportBean{" +
                "bill=" + bill +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

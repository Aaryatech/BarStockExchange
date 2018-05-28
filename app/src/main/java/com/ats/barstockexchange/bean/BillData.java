package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 2/12/17.
 */

public class BillData {

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
        return "BillData{" +
                "bill=" + bill +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

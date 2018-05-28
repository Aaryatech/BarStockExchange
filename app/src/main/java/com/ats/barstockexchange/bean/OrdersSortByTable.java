package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 15/11/17.
 */

public class OrdersSortByTable {

    private List<OrdersByTable> ordersByTable;
    private ErrorMessage errorMessage;

    public List<OrdersByTable> getOrdersByTable() {
        return ordersByTable;
    }

    public void setOrdersByTable(List<OrdersByTable> ordersByTable) {
        this.ordersByTable = ordersByTable;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }


    @Override
    public String toString() {
        return "OrdersSortByTable{" +
                "ordersByTable=" + ordersByTable +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

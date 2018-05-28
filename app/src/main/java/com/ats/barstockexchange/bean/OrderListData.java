package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 15/11/17.
 */

public class OrderListData {

    private List<OrderDispByQuery> orderDispByQuery;
    private ErrorMessage errorMessage;

    public List<OrderDispByQuery> getOrderDispByQuery() {
        return orderDispByQuery;
    }

    public void setOrderDispByQuery(List<OrderDispByQuery> orderDispByQuery) {
        this.orderDispByQuery = orderDispByQuery;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "OrderListData{" +
                "orderDispByQuery=" + orderDispByQuery +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

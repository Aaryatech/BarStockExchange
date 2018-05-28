package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 15/11/17.
 */

public class OrdersByTable {

    private Integer tableNo;
    private List<OrderDisplay> orderDisplay;

    public Integer getTableNo() {
        return tableNo;
    }

    public void setTableNo(Integer tableNo) {
        this.tableNo = tableNo;
    }

    public List<OrderDisplay> getOrderDisplay() {
        return orderDisplay;
    }

    public void setOrderDisplay(List<OrderDisplay> orderDisplay) {
        this.orderDisplay = orderDisplay;
    }

    @Override
    public String toString() {
        return "OrdersByTable{" +
                "tableNo=" + tableNo +
                ", orderDisplay=" + orderDisplay +
                '}';
    }
}

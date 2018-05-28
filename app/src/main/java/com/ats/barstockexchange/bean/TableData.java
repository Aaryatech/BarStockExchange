package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 11/11/17.
 */

public class TableData {

    private List<Table> table;
    private ErrorMessage errorMessage;

    public List<Table> getTable() {
        return table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "TableData{" +
                "table=" + table +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

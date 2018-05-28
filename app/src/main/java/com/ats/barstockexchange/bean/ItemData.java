package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 8/11/17.
 */

public class ItemData {

    private List<Item> item;
    private ErrorMessage errorMessage;

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ItemData{" +
                "item=" + item +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

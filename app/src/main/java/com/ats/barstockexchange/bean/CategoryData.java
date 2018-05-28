package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 3/11/17.
 */

public class CategoryData {

    private List<Category> category;
    private ErrorMessage errorMessage;

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "CategoryData{" +
                "category=" + category +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

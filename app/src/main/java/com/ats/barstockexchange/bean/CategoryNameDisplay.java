package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 8/11/17.
 */

public class CategoryNameDisplay {

    private List<CategoryNameList> categoryNameList;
    private ErrorMessage errorMessage;

    public List<CategoryNameList> getCategoryNameList() {
        return categoryNameList;
    }

    public void setCategoryNameList(List<CategoryNameList> categoryNameList) {
        this.categoryNameList = categoryNameList;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "CategoryNameDisplay{" +
                "categoryNameList=" + categoryNameList +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

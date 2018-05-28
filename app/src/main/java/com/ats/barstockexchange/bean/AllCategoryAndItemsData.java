package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 13/11/17.
 */

public class AllCategoryAndItemsData {

   // private List<Category> category;
    private List<CategoryItemList> categoryItemList;
    private ErrorMessage errorMessage;
    private Settings settings;


    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<CategoryItemList> getCategoryItemList() {
        return categoryItemList;
    }

    public void setCategoryItemList(List<CategoryItemList> categoryItemList) {
        this.categoryItemList = categoryItemList;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "AllCategoryAndItemsData{" +
                "categoryItemList=" + categoryItemList +
                ", errorMessage=" + errorMessage +
                ", settings=" + settings +
                '}';
    }
}

package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 13/11/17.
 */

public class CategoryItemList {

    private Integer catId;
    private String categoryName;
    private String image;
    private Integer totalItems;
    private List<Item> item;

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public List<Item> getItem() {
        return item;
    }

    public void setItem(List<Item> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "CategoryItemList{" +
                "catId=" + catId +
                ", categoryName='" + categoryName + '\'' +
                ", image='" + image + '\'' +
                ", totalItems=" + totalItems +
                ", item=" + item +
                '}';
    }
}



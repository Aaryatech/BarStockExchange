package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 8/11/17.
 */

public class Item {

    private Integer itemId;
    private String itemName;
    private String itemDesc;
    private String itemImage;
    private float mrpGame;
    private float mrpRegular;
    private float mrpSpecial;
    private float openingRate;
    private float maxRate;
    private float minRate;
    private Integer currentStock;
    private Integer catId;
    private float sgst;
    private float cgst;
    private Integer isMixerApplicable;
    private Integer userId;
    private String updatedDate;
    private Integer delStatus;
    private Integer minStock;


    public Item(Integer itemId, String itemName, String itemDesc, String itemImage, float mrpGame, float mrpRegular, float mrpSpecial, float openingRate, float maxRate, float minRate, Integer currentStock, Integer catId, float sgst, float cgst, Integer isMixerApplicable, Integer userId, String updatedDate, Integer delStatus, Integer minStock) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemImage = itemImage;
        this.mrpGame = mrpGame;
        this.mrpRegular = mrpRegular;
        this.mrpSpecial = mrpSpecial;
        this.openingRate = openingRate;
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.currentStock = currentStock;
        this.catId = catId;
        this.sgst = sgst;
        this.cgst = cgst;
        this.isMixerApplicable = isMixerApplicable;
        this.userId = userId;
        this.updatedDate = updatedDate;
        this.delStatus = delStatus;
        this.minStock = minStock;

    }

    public Item(String itemName, String itemDesc, String itemImage, float mrpGame, float mrpRegular, float mrpSpecial, float openingRate, float maxRate, float minRate, Integer currentStock, Integer catId, float sgst, float cgst, Integer isMixerApplicable, Integer userId, String updatedDate, Integer delStatus, Integer minStock) {
        this.itemName = itemName;
        this.itemDesc = itemDesc;
        this.itemImage = itemImage;
        this.mrpGame = mrpGame;
        this.mrpRegular = mrpRegular;
        this.mrpSpecial = mrpSpecial;
        this.openingRate = openingRate;
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.currentStock = currentStock;
        this.catId = catId;
        this.sgst = sgst;
        this.cgst = cgst;
        this.isMixerApplicable = isMixerApplicable;
        this.userId = userId;
        this.updatedDate = updatedDate;
        this.delStatus = delStatus;
        this.minStock = minStock;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public float getMrpGame() {
        return mrpGame;
    }

    public void setMrpGame(float mrpGame) {
        this.mrpGame = mrpGame;
    }

    public float getMrpRegular() {
        return mrpRegular;
    }

    public void setMrpRegular(float mrpRegular) {
        this.mrpRegular = mrpRegular;
    }

    public float getMrpSpecial() {
        return mrpSpecial;
    }

    public void setMrpSpecial(float mrpSpecial) {
        this.mrpSpecial = mrpSpecial;
    }

    public float getOpeningRate() {
        return openingRate;
    }

    public void setOpeningRate(float openingRate) {
        this.openingRate = openingRate;
    }

    public float getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(float maxRate) {
        this.maxRate = maxRate;
    }

    public float getMinRate() {
        return minRate;
    }

    public void setMinRate(float minRate) {
        this.minRate = minRate;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public float getSgst() {
        return sgst;
    }

    public void setSgst(float sgst) {
        this.sgst = sgst;
    }

    public float getCgst() {
        return cgst;
    }

    public void setCgst(float cgst) {
        this.cgst = cgst;
    }

    public Integer getIsMixerApplicable() {
        return isMixerApplicable;
    }

    public void setIsMixerApplicable(Integer isMixerApplicable) {
        this.isMixerApplicable = isMixerApplicable;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    @Override
    public String toString() {
        return "Item{" +
                "itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", itemDesc='" + itemDesc + '\'' +
                ", itemImage='" + itemImage + '\'' +
                ", mrpGame=" + mrpGame +
                ", mrpRegular=" + mrpRegular +
                ", mrpSpecial=" + mrpSpecial +
                ", openingRate=" + openingRate +
                ", maxRate=" + maxRate +
                ", minRate=" + minRate +
                ", currentStock=" + currentStock +
                ", catId=" + catId +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                ", isMixerApplicable=" + isMixerApplicable +
                ", userId=" + userId +
                ", updatedDate='" + updatedDate + '\'' +
                ", delStatus=" + delStatus +
                ", minStock=" + minStock +
                '}';
    }
}

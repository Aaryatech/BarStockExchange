package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 13/11/17.
 */

public class News {

    private Integer newsId;
    private String newsTitle;
    private String newsImage;
    private String newsDesc;
    private String newsDate;
    private Integer isDelete;
    private Integer userId;
    private String updatedDate;

    public News(Integer newsId, String newsTitle, String newsImage, String newsDesc, String newsDate, Integer isDelete, Integer userId, String updatedDate) {
        this.newsId = newsId;
        this.newsTitle = newsTitle;
        this.newsImage = newsImage;
        this.newsDesc = newsDesc;
        this.newsDate = newsDate;
        this.isDelete = isDelete;
        this.userId = userId;
        this.updatedDate = updatedDate;
    }

    public News(String newsTitle, String newsImage, String newsDesc, String newsDate, Integer isDelete, Integer userId, String updatedDate) {
        this.newsTitle = newsTitle;
        this.newsImage = newsImage;
        this.newsDesc = newsDesc;
        this.newsDate = newsDate;
        this.isDelete = isDelete;
        this.userId = userId;
        this.updatedDate = updatedDate;
    }

    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsImage() {
        return newsImage;
    }

    public void setNewsImage(String newsImage) {
        this.newsImage = newsImage;
    }

    public String getNewsDesc() {
        return newsDesc;
    }

    public void setNewsDesc(String newsDesc) {
        this.newsDesc = newsDesc;
    }

    public String getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(String newsDate) {
        this.newsDate = newsDate;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
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

    @Override
    public String toString() {
        return "News{" +
                "newsId=" + newsId +
                ", newsTitle='" + newsTitle + '\'' +
                ", newsImage='" + newsImage + '\'' +
                ", newsDesc='" + newsDesc + '\'' +
                ", newsDate='" + newsDate + '\'' +
                ", isDelete=" + isDelete +
                ", userId=" + userId +
                ", updatedDate='" + updatedDate + '\'' +
                '}';
    }
}

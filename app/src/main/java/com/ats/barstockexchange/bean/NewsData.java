package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 13/11/17.
 */

public class NewsData {

    private List<News> news;
    private ErrorMessage errorMessage;

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "NewsData{" +
                "news=" + news +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

package com.ats.barstockexchange.bean;

import java.sql.Date;

/**
 * Created by maxadmin on 13/11/17.
 */

public class Settings {

    private int settingId;
    private String appMode;
    private double latitude;
    private double longitude;
    private int radius;
    private int userId;
    private String updatedDate;

    public Settings(int settingId, String appMode, double latitude, double longitude, int radius, int userId, String updatedDate) {
        this.settingId = settingId;
        this.appMode = appMode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.userId = userId;
        this.updatedDate = updatedDate;
    }


    public int getSettingId() {
        return settingId;
    }

    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

    public String getAppMode() {
        return appMode;
    }

    public void setAppMode(String appMode) {
        this.appMode = appMode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
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
        return "Settings{" +
                "settingId=" + settingId +
                ", appMode='" + appMode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", radius=" + radius +
                ", userId=" + userId +
                ", updatedDate=" + updatedDate +
                '}';
    }
}

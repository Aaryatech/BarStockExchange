package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 14/11/17.
 */

public class User {

    private Integer userId;
    private String firstname;
    private String mobile;
    private String password;
    private Integer isActive;
    private Integer delStatus;
    private Integer enterBy;
    private String deviceToken;
    private String email;
    private String dob;

    public User(Integer userId, String firstname, String mobile, String password, Integer isActive, Integer delStatus, Integer enterBy, String deviceToken) {
        this.userId = userId;
        this.firstname = firstname;
        this.mobile = mobile;
        this.password = password;
        this.isActive = isActive;
        this.delStatus = delStatus;
        this.enterBy = enterBy;
        this.deviceToken = deviceToken;
    }

    public User(String firstname, String mobile, String password, Integer isActive, Integer delStatus, Integer enterBy, String deviceToken) {
        this.firstname = firstname;
        this.mobile = mobile;
        this.password = password;
        this.isActive = isActive;
        this.delStatus = delStatus;
        this.enterBy = enterBy;
        this.deviceToken = deviceToken;
    }

    public User(String firstname, String mobile, String password, Integer isActive, Integer delStatus, Integer enterBy, String deviceToken, String email, String dob) {
        this.firstname = firstname;
        this.mobile = mobile;
        this.password = password;
        this.isActive = isActive;
        this.delStatus = delStatus;
        this.enterBy = enterBy;
        this.deviceToken = deviceToken;
        this.email = email;
        this.dob = dob;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Integer getEnterBy() {
        return enterBy;
    }

    public void setEnterBy(Integer enterBy) {
        this.enterBy = enterBy;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstname='" + firstname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                ", delStatus=" + delStatus +
                ", enterBy=" + enterBy +
                ", deviceToken='" + deviceToken + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                '}';
    }
}

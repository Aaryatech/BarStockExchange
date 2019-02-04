package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 3/11/17.
 */

public class Admin {

    private Integer adminId;
    private String username;
    private String password;
    private String type;
    private Integer delStatus;
    private String token;

    public Admin(Integer adminId, String username, String password, String type, Integer delStatus) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
        this.type = type;
        this.delStatus = delStatus;
    }

    public Admin(String username, String password, String type, Integer delStatus) {
        this.username = username;
        this.password = password;
        this.type = type;
        this.delStatus = delStatus;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", type='" + type + '\'' +
                ", delStatus=" + delStatus +
                '}';
    }
}

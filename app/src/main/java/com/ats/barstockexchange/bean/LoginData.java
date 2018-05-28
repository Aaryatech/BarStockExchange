package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 3/11/17.
 */

public class LoginData {

    private String type;
    private Integer id;
    private String name;
    private String email;
    private String mobile;
    private String image;
    private String socialKey;
    private String token;
    private Integer radius;
    private ErrorMessage errorMessage;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSocialKey() {
        return socialKey;
    }

    public void setSocialKey(String socialKey) {
        this.socialKey = socialKey;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", image='" + image + '\'' +
                ", socialKey='" + socialKey + '\'' +
                ", token='" + token + '\'' +
                ", radius=" + radius +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

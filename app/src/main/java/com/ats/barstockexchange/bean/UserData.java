package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 3/11/17.
 */

public class UserData {

    private List<Admin> admin;
    private ErrorMessage errorMessage;

    public List<Admin> getAdmin() {
        return admin;
    }

    public void setAdmin(List<Admin> admin) {
        this.admin = admin;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String
    toString() {
        return "UserData{" +
                "admin=" + admin +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

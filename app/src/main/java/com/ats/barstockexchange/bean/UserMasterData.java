package com.ats.barstockexchange.bean;

import java.util.List;

/**
 * Created by maxadmin on 14/11/17.
 */

public class UserMasterData {

    private List<User> user;
    private ErrorMessage errorMessage;

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "UserMasterData{" +
                "user=" + user +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

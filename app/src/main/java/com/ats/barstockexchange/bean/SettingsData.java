package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 13/11/17.
 */

public class SettingsData {

    private Settings settings;
    private ErrorMessage errorMessage;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "SettingsData{" +
                "settings=" + settings +
                ", errorMessage=" + errorMessage +
                '}';
    }
}

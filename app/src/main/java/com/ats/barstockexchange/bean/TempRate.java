package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 13/11/17.
 */

public class TempRate {
    private String min;
    private String max;

    public TempRate() {
    }

    public TempRate(String min, String max) {
        this.min = min;
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "TempRate{" +
                "min='" + min + '\'' +
                ", max='" + max + '\'' +
                '}';
    }
}

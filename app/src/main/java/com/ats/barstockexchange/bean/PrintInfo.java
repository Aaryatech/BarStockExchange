package com.ats.barstockexchange.bean;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by MAXADMIN on 27/2/2018.
 */

public class PrintInfo {

    String PrintAddress;
    String PrintName;
    int Model;
    int PrintReceiptType;

    public String getPrintAddress() {
        return PrintAddress;
    }

    public void setPrintAddress(String printAddress) {
        PrintAddress = printAddress;
    }

    public String getPrintName() {
        return PrintName;
    }

    public void setPrintName(String printName) {
        PrintName = printName;
    }

    public int getModel() {
        return Model;
    }

    public void setModel(int model) {
        Model = model;
    }

    @Override
    public String toString() {
        return "PrintInfo{" +
                "PrintAddress='" + PrintAddress + '\'' +
                ", PrintName='" + PrintName + '\'' +
                ", Model=" + Model +
                ", PrintReceiptType=" + PrintReceiptType +
                '}';
    }
}

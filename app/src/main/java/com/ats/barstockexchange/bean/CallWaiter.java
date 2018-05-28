package com.ats.barstockexchange.bean;

/**
 * Created by MAXADMIN on 27/1/2018.
 */

public class CallWaiter {

    private int id;
    private int userId;
    private int tableNo;
    private String date;
    private int isVisited;

    public CallWaiter(int userId, int tableNo, String date, int isVisited) {
        this.userId = userId;
        this.tableNo = tableNo;
        this.date = date;
        this.isVisited = isVisited;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTableNo() {
        return tableNo;
    }

    public void setTableNo(int tableNo) {
        this.tableNo = tableNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIsVisited() {
        return isVisited;
    }

    public void setIsVisited(int isVisited) {
        this.isVisited = isVisited;
    }

    @Override
    public String toString() {
        return "CallWaiter{" +
                "id=" + id +
                ", userId=" + userId +
                ", tableNo=" + tableNo +
                ", date='" + date + '\'' +
                ", isVisited=" + isVisited +
                '}';
    }
}

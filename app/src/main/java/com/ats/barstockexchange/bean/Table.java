package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 11/11/17.
 */

public class Table {

    private Integer tableId;
    private Integer tableNo;
    private String tableName;
    private Integer isDelete;
    private Integer isActive;
    private Integer userId;
    private String updatedDate;

    public Table(Integer tableId, Integer tableNo, String tableName, Integer isDelete, Integer isActive, Integer userId, String updatedDate) {
        this.tableId = tableId;
        this.tableNo = tableNo;
        this.tableName = tableName;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.userId = userId;
        this.updatedDate = updatedDate;
    }

    public Table(Integer tableNo, String tableName, Integer isDelete, Integer isActive, Integer userId, String updatedDate) {
        this.tableNo = tableNo;
        this.tableName = tableName;
        this.isDelete = isDelete;
        this.isActive = isActive;
        this.userId = userId;
        this.updatedDate = updatedDate;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getTableNo() {
        return tableNo;
    }

    public void setTableNo(Integer tableNo) {
        this.tableNo = tableNo;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
        return "Table{" +
                "tableId=" + tableId +
                ", tableNo=" + tableNo +
                ", tableName='" + tableName + '\'' +
                ", isDelete=" + isDelete +
                ", isActive=" + isActive +
                ", userId=" + userId +
                ", updatedDate='" + updatedDate + '\'' +
                '}';
    }
}

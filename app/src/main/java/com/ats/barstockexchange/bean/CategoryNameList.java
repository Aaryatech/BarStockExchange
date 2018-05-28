package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 8/11/17.
 */

public class CategoryNameList {

    private String name;
    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CategoryNameList{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}

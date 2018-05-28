package com.ats.barstockexchange.bean;

/**
 * Created by maxadmin on 30/11/17.
 */

public class TempDataBean {

    private int id;
    private String name;
    private float price;
    private int qty;
    private float sgst;
    private float cgst;
    private int isMixer;

    public TempDataBean(int id, String name, float price, int qty, float sgst, float cgst) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.sgst = sgst;
        this.cgst = cgst;
    }

    public TempDataBean(int id, String name, float price, int qty) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public TempDataBean(int id, String name, float price, int qty, float sgst, float cgst, int isMixer) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.sgst = sgst;
        this.cgst = cgst;
        this.isMixer = isMixer;
    }

    public int getIsMixer() {
        return isMixer;
    }

    public void setIsMixer(int isMixer) {
        this.isMixer = isMixer;
    }

    public float getSgst() {
        return sgst;
    }

    public void setSgst(float sgst) {
        this.sgst = sgst;
    }

    public float getCgst() {
        return cgst;
    }

    public void setCgst(float cgst) {
        this.cgst = cgst;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "TempDataBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", qty=" + qty +
                ", sgst=" + sgst +
                ", cgst=" + cgst +
                '}';
    }
}

package com.globalshops.models;

public class ShoeSizeQuantity {
    private String size;
    private String quantity;

    public ShoeSizeQuantity() {
    }

    public ShoeSizeQuantity(String size, String quantity) {
        this.size = size;
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

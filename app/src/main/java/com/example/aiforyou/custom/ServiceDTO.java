package com.example.aiforyou.custom;

public class ServiceDTO {
    private int id;
    private String tenService, imageUrl;
    private float price;

    public ServiceDTO() {
    }

    public int getId() {
        return id;
    }

    public String getTenService() {
        return tenService;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public float getPrice() {
        return price;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTenService(String tenService) {
        this.tenService = tenService;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
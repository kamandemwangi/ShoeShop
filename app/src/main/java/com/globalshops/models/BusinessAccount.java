package com.globalshops.models;

public class BusinessAccount {
    private String name;
    private String email;
    private String phone;
    private String Street;
    private String businessBuilding;
    private String shopNumber;
    private String shopId;
    private String profileImageName;
    private String profileImageUrl;
    private boolean isAuthenticated;

    public BusinessAccount() {
    }

    public BusinessAccount(String name, String email, String phone, String street, String businessBuilding, String shopNumber, String profileImageName, String profileImageUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        Street = street;
        this.businessBuilding = businessBuilding;
        this.shopNumber = shopNumber;
        this.profileImageName = profileImageName;
        this.profileImageUrl = profileImageUrl;
    }

    public BusinessAccount(String name, String phone, String street, String businessBuilding, String shopNumber) {
        this.name = name;
        this.phone = phone;
        Street = street;
        this.businessBuilding = businessBuilding;
        this.shopNumber = shopNumber;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getBusinessBuilding() {
        return businessBuilding;
    }

    public void setBusinessBuilding(String businessBuilding) {
        this.businessBuilding = businessBuilding;
    }

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public String getProfileImageName() {
        return profileImageName;
    }

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

package com.adino.capstone.model;

public class Agency {

    private String name;
    private String info;
    private String imageURL;
    private String phone;

    public Agency() {
    }

    public Agency(String name, String info, String imageURL, String phone) {
        setName(name);
        setInfo(info);
        setImageURL(imageURL);
        setPhone(phone);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

package com.adino.capstone.model;

public class Agency {

    private String name;
    private String info;
    private String imageURL;
    private Long phone;

    public Agency() {
    }

    public Agency(String name, String info, String imageURL, Long phone) {
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

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }
}

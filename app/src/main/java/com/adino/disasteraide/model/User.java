package com.adino.disasteraide.model;

public class User {

    private String userID;
    private String subscriptions;
    private double longitude;
    private double latitude;

    public User() {
    }

    public User(String userID, String subscriptions) {
        setSubscriptions(subscriptions);
        setUserID(userID);
        setLatitude(1);
        setLongitude(-1);
    }

    public User(String userID, String subscriptions, double latitude, double longitude) {
        setSubscriptions(subscriptions);
        setUserID(userID);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(String subscriptions) {
        this.subscriptions = subscriptions;
    }
}

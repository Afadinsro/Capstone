package com.adino.capstone.model;

public class User {

    private String userID;
    private String subscriptions;

    public User() {
    }

    public User(String userID, String subscriptions) {
        setSubscriptions(subscriptions);
        setUserID(userID);
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

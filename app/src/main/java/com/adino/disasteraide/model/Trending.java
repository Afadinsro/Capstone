package com.adino.disasteraide.model;

/**
 * Created by afadinsro on 3/23/18.
 */

public class Trending {
    private String title;
    private String details;
    private String imageURL;
    private String topic;
    private boolean status;
    private double latitude;
    private double longitude;

    /**
     * Default constructor
     * Required by Firebase
     */
    public Trending() {
    }

    public Trending(String title, String details, String imageURL, String topic, boolean status,
                    double latitude, double longitude) {
        setTitle(title);
        setDetails(details);
        setImageURL(imageURL);
        setStatus(status);
        setTopic(topic);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

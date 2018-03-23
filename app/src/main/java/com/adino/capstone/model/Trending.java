package com.adino.capstone.model;

/**
 * Created by afadinsro on 3/23/18.
 */

public class Trending {
    private String title;
    private String description;
    private String imageURL;
    private boolean status;

    /**
     * Default constructor
     * Required by Firebase
     */
    public Trending() {
    }

    public Trending(String title, String description, String imageURL, boolean status) {
        setTitle(title);
        setDescription(description);
        setImageURL(imageURL);
        setStatus(status);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}

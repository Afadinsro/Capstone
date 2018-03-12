package com.adino.capstone.model;

/**
 * Created by afadinsro on 12/6/17.
 */

public class Report {
    private String category;
    private String caption;
    private String date;
    private String imageURL;
    private String location;

    /**
     * Default constructor required by Firebase
     */
    public Report() {}

    /**
     * Constructor
     * @param caption caption
     * @param date date
     * @param category category
     * @param imageURL image URL
     * @param location location
     */
    public Report(String caption, String date, String category, String imageURL, String location) {
        setCaption(caption);
        setCategory(category);
        setDate(date);
        setImageURL(imageURL);
        setLocation(location);
    }
    /****************************SETTER METHODS***********************************/
    /**
     * Set category
     * @param category category
     */
    private void setCategory(String category) {
        this.category = category;
    }

    /**
     * Set caption
     * @param caption caption
     */
    private void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Set date
     * @param date date
     */
    private void setDate(String date) {
        this.date = date;
    }

    /**
     * Set image URL
     * @param imageURL image URL
     */
    private void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * Set location
     * @param location location
     */
    private void setLocation(String location) {
        this.location = location;
    }

    /****************************GETTER METHODS***********************************/
    /**
     * Get category
     * @return Report category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Get caption
     * @return Report acption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Get date
     * @return Report date
     */
    public String getDate() {
        return date;
    }

    /**
     * Get image URL
     * @return image URL
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Get location
     * @return location
     */
    public String getLocation() {
        return location;
    }
}

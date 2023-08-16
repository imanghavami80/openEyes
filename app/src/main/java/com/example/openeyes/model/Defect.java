package com.example.openeyes.model;

public class Defect {

    private String category;
    private String location;
    private String description;
    private int likes;
    private float rate;

    public Defect(String category, String location, String description, int likes, float rate) {
        this.category = category;
        this.location = location;
        this.description = description;
        this.likes = likes;
        this.rate = rate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

}

package com.example.openeyes.model;

import android.net.Uri;

import java.util.ArrayList;

public class Defect {

    private String location;
    private double latitude;
    private double longitude;
    private String category;
    private String description;
    private String date;
    private int likes;
    private float rate;


    public Defect(String category, String location, String description, int likes, float rate) {
        this.category = category;
        this.location = location;
        this.description = description;
        this.likes = likes;
        this.rate = rate;
    }

    public Defect(String location, double latitude, double longitude, String category, String description, String date, int likes, float rate) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.description = description;
        this.date = date;
        this.likes = likes;
        this.rate = rate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

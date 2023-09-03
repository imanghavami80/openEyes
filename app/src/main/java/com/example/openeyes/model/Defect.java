package com.example.openeyes.model;

import android.net.Uri;

import java.util.ArrayList;

public class Defect {

    protected String location;
    protected double latitude;
    protected double longitude;
    protected String category;
    protected String description;
    protected String date;
    protected int likes;
    protected float rate;
    protected int haveImage; // (0 -> no image) - (anything but 0 -> number of images)
    protected int haveAudio; // (0 -> no audio) - (1 -> have audio)


    public Defect(String location, String category, String description, int likes, float rate, int haveImage, int haveAudio) {
        this.location = location;
        this.category = category;
        this.description = description;
        this.likes = likes;
        this.rate = rate;
        this.haveImage = haveImage;
        this.haveAudio = haveAudio;
    }

    public Defect(String location, double latitude, double longitude, String category, String description, String date, int likes, float rate, int haveImage, int haveAudio) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
        this.description = description;
        this.date = date;
        this.likes = likes;
        this.rate = rate;
        this.haveImage = haveImage;
        this.haveAudio = haveAudio;
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

    public int getHaveImage() {
        return haveImage;
    }

    public void setHaveImage(int haveImage) {
        this.haveImage = haveImage;
    }

    public int getHaveAudio() {
        return haveAudio;
    }

    public void setHaveAudio(int haveAudio) {
        this.haveAudio = haveAudio;
    }
}

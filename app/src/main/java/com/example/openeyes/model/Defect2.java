package com.example.openeyes.model;

import java.util.ArrayList;

public class Defect2 extends Defect {

    private String uuid;
    private String email;
    private String firstImage;
    private ArrayList<String> images;

    public Defect2(String location, String category, String description, int likes, float rate, int haveImage, int haveAudio, String uuid, String email) {
        super(location, category, description, likes, rate, haveImage, haveAudio);
        this.uuid = uuid;
        this.email = email;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstImage() {
        return firstImage;
    }

    public void setFirstImage(String firstImage) {
        this.firstImage = firstImage;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}

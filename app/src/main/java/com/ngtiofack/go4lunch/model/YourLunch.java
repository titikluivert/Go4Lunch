package com.ngtiofack.go4lunch.model;


public class YourLunch {


    private String name;
    private String vicinity;
    private int photoHeight;
    private int photoWidth;
    private String photoUrlRef;
    private int ratingStars;

    public YourLunch() {

    }

    public YourLunch(int ratingStars) {
        this.ratingStars = ratingStars;
    }

    public YourLunch(String name, String vicinity, int photoHeight, int photoWidth, String photoUrlRef, int ratingStars) {
        this.name = name;
        this.vicinity = vicinity;
        this.photoHeight = photoHeight;
        this.photoWidth = photoWidth;
        this.photoUrlRef = photoUrlRef;
        this.ratingStars = ratingStars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public int getPhotoHeight() {
        return photoHeight;
    }

    public void setPhotoHeight(int photoHeight) {
        this.photoHeight = photoHeight;
    }

    public int getPhotoWidth() {
        return photoWidth;
    }

    public void setPhotoWidth(int photoWidth) {
        this.photoWidth = photoWidth;
    }

    public String getPhotoUrlRef() {
        return photoUrlRef;
    }

    public void setPhotoUrlRef(String photoUrlRef) {
        this.photoUrlRef = photoUrlRef;
    }

    public int getRatingStars() {
        return ratingStars;
    }

    public void setRatingStars(int ratingStars) {
        this.ratingStars = ratingStars;
    }
}

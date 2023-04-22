package com.example.hopreviews.data.model;

public class Review {
    private final String review;
    private final String date;
    private final String location;
    private final String user;
    private final float rating;

    public Review(String review, String date, String location, String user, float rating) {
        this.review = review;
        this.date = date;
        this.location = location;
        this.user = user;
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    public String getReview() {
        return review;
    }

    public String getUser() {
        return user;
    }

    public String getLocation() {
        return location;
    }
}

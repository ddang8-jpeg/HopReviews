package com.example.hopreviews.data.model;

public class LocationReview {
    private final String review;
    private final String date;
    private final String user;
    private final float rating;

    public LocationReview(String review, String date, String user, float rating) {
        this.review = review;
        this.date = date;
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
}

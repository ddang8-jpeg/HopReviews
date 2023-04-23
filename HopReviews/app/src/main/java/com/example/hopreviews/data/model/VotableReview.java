package com.example.hopreviews.data.model;

import java.util.ArrayList;

public class VotableReview {
    private final String review;
    private final String date;
    private final String location;
    private final String user;
    private final float rating;
    private final String timestamp;
    private final ArrayList<String> upvotes;
    private final ArrayList<String> downvotes;

    public VotableReview(String review, String date, String location,
                         String user, float rating, String timestamp,
                         ArrayList<String> upvotes, ArrayList<String> downvotes) {
        this.review = review;
        this.date = date;
        this.location = location;
        this.user = user;
        this.rating = rating;
        this.timestamp = timestamp;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
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

    public String getTimestamp() { return timestamp; }

    public ArrayList<String> getDownvotes() {
        return downvotes;
    }

    public ArrayList<String> getUpvotes() {
        return upvotes;
    }
}

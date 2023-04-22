package com.example.hopreviews.data.model;

import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

// a lot of this code is derived from its Kotlin version on an open source
// android tutorial https://developer.android.com/codelabs/basic-android-kotlin-training-display-list-cards#3
public class Image {
    private final Uri imageResourceId;

    public Image(Uri uri) {
        this.imageResourceId = uri;
    }

    public Uri getImageResourceId() {
        return imageResourceId;
    }
}


package com.barmej.astronomypicture.entity;

import android.net.Uri;

public class Astronomy {
    private String title;
    private String explanation;
    private String mediaType;
    private Uri hdurl;
    private Uri uri;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Uri getHdurl() {
        return hdurl;
    }

    public void setHdurl(Uri hdurl) {
        this.hdurl = hdurl;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}

package com.globalshops.models;

public class UploadProfileImageTask {
    private String imageUrl;
    private boolean isUploaded;

    public UploadProfileImageTask(String imageUrl, boolean isUploaded) {
        this.imageUrl = imageUrl;
        this.isUploaded = isUploaded;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}

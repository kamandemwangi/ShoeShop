package com.globalshops.models;

import java.util.List;

public class UploadImagesTask {
    private List<String> imagesUrl;
    private boolean isImagesUploaded;

    public UploadImagesTask() {
    }

    public UploadImagesTask(List<String> imagesUrl, boolean isImagesUploaded) {
        this.imagesUrl = imagesUrl;
        this.isImagesUploaded = isImagesUploaded;
    }

    public List<String> getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public boolean isImagesUploaded() {
        return isImagesUploaded;
    }

    public void setImagesUploaded(boolean imagesUploaded) {
        isImagesUploaded = imagesUploaded;
    }
}

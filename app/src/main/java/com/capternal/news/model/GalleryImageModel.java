package com.capternal.news.model;

/**
 * Created by jupitor on 26/10/17.
 */

public class GalleryImageModel {

    private String ImageUrl = "";
    private String Description = "";

    public GalleryImageModel(String imageUrl, String description) {
        ImageUrl = imageUrl;
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}

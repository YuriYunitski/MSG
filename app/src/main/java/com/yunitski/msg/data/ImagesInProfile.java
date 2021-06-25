package com.yunitski.msg.data;

public class ImagesInProfile {

    private String imageUrl;
    private String urlString;

    public ImagesInProfile(String imageUrl, String urlString) {
        this.imageUrl = imageUrl;
        this.urlString = urlString;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }
}

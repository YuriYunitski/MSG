package com.yunitski.msg.data;

public class AudioInProfile {

    private String audioUrl;

    private String audioName;

    public AudioInProfile() {
    }


    public AudioInProfile(String audioUrl, String audioName) {
        this.audioUrl = audioUrl;
        this.audioName = audioName;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }
}

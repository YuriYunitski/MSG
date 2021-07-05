package com.yunitski.msg.data;

public class MSGmessage {
    private String text;
    private String name;
    private String imageUrl;
    private String sender;
    private String recipient;
    private boolean isMine;
    private String time;
    private boolean isDeleted;
    private String pusId;
    private boolean isRead;
    private String videoUrl;
    private String audioUrl;
    private String audioName;
    private String audioLocation;
    private String fileName;
    private String fileUrl;

    public MSGmessage() {
    }

    public MSGmessage(String text, String name, String imageUrl, String sender, String recipient, boolean isMine, String time, boolean isDeleted, String pusId, boolean isRead, String videoUrl, String audioUrl, String audioName, String audioLocation, String fileName, String fileUrl) {
        this.text = text;
        this.name = name;
        this.imageUrl = imageUrl;
        this.sender = sender;
        this.recipient = recipient;
        this.isMine = isMine;
        this.time = time;
        this.isDeleted = isDeleted;
        this.pusId = pusId;
        this.isRead = isRead;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.audioName = audioName;
        this.audioLocation = audioLocation;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getPusId() {
        return pusId;
    }

    public void setPusId(String pusId) {
        this.pusId = pusId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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

    public String getAudioLocation() {
        return audioLocation;
    }

    public void setAudioLocation(String audioLocation) {
        this.audioLocation = audioLocation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}

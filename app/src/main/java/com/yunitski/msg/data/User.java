package com.yunitski.msg.data;

public class User {
    private String name;
    private String email;
    private String id;
    private int avatarMockupResource;
    private String lastMessage;
    private String profilePhotoUrl;
    private String pushId;
    private String password;

    public User() {
    }

    public User(String name, String email, String id, int avatarMockupResource, String lastMessage, String profilePhotoUrl, String pushId, String password) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.avatarMockupResource = avatarMockupResource;
        this.lastMessage = lastMessage;
        this.profilePhotoUrl = profilePhotoUrl;
        this.pushId = pushId;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAvatarMockupResource() {
        return avatarMockupResource;
    }

    public void setAvatarMockupResource(int avatarMockupResource) {
        this.avatarMockupResource = avatarMockupResource;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

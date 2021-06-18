package com.yunitski.msg.data;

public class User {
    private String name;
    private String email;
    private String id;
    private int avatarMockupResource;

    public User() {
    }

    public User(String name, String email, String id, int avatarMockupResource) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.avatarMockupResource = avatarMockupResource;
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
}

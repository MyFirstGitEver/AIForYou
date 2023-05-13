package com.example.aiforyou.custom;

public class WorkBoxDTO {
    private final String userName, sharedWorkTitle, path;

    public WorkBoxDTO(String userName, String sharedWorkTitle, String path) {
        this.userName = userName;
        this.sharedWorkTitle = sharedWorkTitle;
        this.path = path;
    }

    public String getUserName() {
        return userName;
    }

    public String getSharedWorkTitle() {
        return sharedWorkTitle;
    }

    public String getPath() {
        return path;
    }
}
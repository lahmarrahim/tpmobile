package com.example.monderniertp;

public class MusicFiles {
    private String path;
    private String title;

    public MusicFiles(String path, String title) {
        this.path = path;
        this.title = title;
    }

    public MusicFiles() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

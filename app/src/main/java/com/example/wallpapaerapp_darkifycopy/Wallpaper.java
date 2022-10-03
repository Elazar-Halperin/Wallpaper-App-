package com.example.wallpapaerapp_darkifycopy;

public class Wallpaper {
    int id;
    String url;

    public Wallpaper() {
    }

    public Wallpaper(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getId() {
        return id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

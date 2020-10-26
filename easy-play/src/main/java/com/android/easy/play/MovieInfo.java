package com.android.easy.play;

import java.io.Serializable;

public class MovieInfo implements Serializable {
    private String name;
    private String url;
    private int num;

    public MovieInfo(String name, String url, int num) {
        this.name = name;
        this.url = url;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}

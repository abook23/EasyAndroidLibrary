package com.android.easy.mediastore.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * author abook23@163.com
 *  2019/11/28
 */
public class LocalMediaFolder {

    private String name;

    private String firstImagePath;

    private int imageNum;

    private List<LocalMedia> images = new ArrayList<LocalMedia>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public List<LocalMedia> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(List<LocalMedia> images) {
        this.images = images;
    }

}

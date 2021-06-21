package com.android.easy.mediastore.utils;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * author abook23@163.com
 *  2019/11/28
 */
public class LocalMedia implements Serializable {
    /**
     * original path
     */
    private String path;
    /**
     * compress path
     */
    private String compressPath;
    /**
     * cut path
     */
    private String cutPath;

    /**
     * Note: this field is only returned in Android Q version
     * <p>
     * Android Q image or video path
     */
    private String androidQToPath;
    /**
     * video duration
     */
    private long duration;
    /**
     * media position of list
     */
    public int position;
    /**
     * The media number of qq choose styles
     */
    private int num;
    /**
     * The media resource type
     */
    private String mimeType;

    /**
     * Gallery selection mode
     */
    private MediaMode mediaMode;

    /**
     * If the compressed
     */
    private boolean compressed;
    /**
     * image or video width
     */
    private int width;
    /**
     * image or video height
     */
    private int height;

    /**
     * file size
     */
    private long size;
    private long dateModified;
    private long dateTaken;

    public LocalMedia() {

    }

    public LocalMedia(String path, long duration, MediaMode mediaMode, String mimeType) {
        this.path = path;
        this.duration = duration;
        this.mediaMode = mediaMode;
        this.mimeType = mimeType;
    }

    public LocalMedia(String path, long duration, MediaMode mediaMode, String mimeType, int width, int height, long size, long dateModified, long dateTaken) {
        this.path = path;
        this.duration = duration;
        this.mediaMode = mediaMode;
        this.mimeType = mimeType;
        this.width = width;
        this.height = height;
        this.size = size;
        this.dateModified = dateModified;
        this.dateTaken = dateTaken;
    }

    public LocalMedia(String path, long duration,int position, int num, MediaMode mediaMode) {
        this.path = path;
        this.duration = duration;
        this.position = position;
        this.num = num;
        this.mediaMode = mediaMode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getCutPath() {
        return cutPath;
    }

    public void setCutPath(String cutPath) {
        this.cutPath = cutPath;
    }

    public String getAndroidQToPath() {
        return androidQToPath;
    }

    public void setAndroidQToPath(String androidQToPath) {
        this.androidQToPath = androidQToPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getMimeType() {
        return TextUtils.isEmpty(mimeType) ? "image/jpeg" : mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public MediaMode getMediaMode() {
        return mediaMode;
    }

    public void setMediaMode(MediaMode mediaMode) {
        this.mediaMode = mediaMode;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }
}

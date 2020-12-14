package com.android.easy.camera;

public interface OnCameraListener {

    /**
     * 预览数据
     * @param bytes
     */
    void onPreviewFrame(byte[] bytes);

    /**
     * 拍照
     * @param bytes
     */
    void onPictureTaken(byte[] bytes);

    void onRecordVideo(String path);
}

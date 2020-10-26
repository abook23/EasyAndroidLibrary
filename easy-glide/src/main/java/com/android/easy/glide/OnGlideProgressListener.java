package com.android.easy.glide;


public interface OnGlideProgressListener {
    void onStart(long max);

    void onProgress(int progress, long max);

    void onComplete();
}

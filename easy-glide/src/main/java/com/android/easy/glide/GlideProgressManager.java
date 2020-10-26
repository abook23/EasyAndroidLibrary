package com.android.easy.glide;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GlideProgressManager {

    private ConcurrentMap<String, OnGlideProgressListener> GLIDE_LISTENER_MAP = new ConcurrentHashMap<>();
    private static GlideProgressManager glideProgressManager = new GlideProgressManager();

    public static GlideProgressManager getInstance() {
        return glideProgressManager;
    }

    public OnGlideProgressListener getListener(String url) {
        return GLIDE_LISTENER_MAP.get(url);
    }

    public void addListener(String url, OnGlideProgressListener onGlideListener) {
        GLIDE_LISTENER_MAP.put(url, onGlideListener);
    }

    public void removeListener(String url) {
        GLIDE_LISTENER_MAP.remove(url);
    }
}

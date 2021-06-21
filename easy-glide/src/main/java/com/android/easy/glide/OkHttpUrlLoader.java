package com.android.easy.glide;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

import okhttp3.OkHttpClient;

public class OkHttpUrlLoader implements ModelLoader<GlideUrl, InputStream> {
    private final okhttp3.Call.Factory client;

    public OkHttpUrlLoader(okhttp3.Call.Factory client) {
        this.client = client;
    }

    public boolean handles(GlideUrl url) {
        return true;
    }

    public LoadData<InputStream> buildLoadData(GlideUrl model, int width, int height, Options options) {
        //返回LoadData对象，泛型为InputStream
        return new LoadData(model, new OkHttpStreamFetcher(this.client, model));
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private static volatile okhttp3.Call.Factory internalClient;
        private okhttp3.Call.Factory client;

        private static okhttp3.Call.Factory getInternalClient(OkHttpClient okHttpClient) {
            if (internalClient == null) {
                synchronized (OkHttpUrlLoader.Factory.class) {
                    if (internalClient == null) {
                        if (okHttpClient==null){
                            internalClient = new OkHttpClient();
                        }else {
                            internalClient = okHttpClient;
                        }
                    }
                }
            }
            return internalClient;
        }

        public Factory() {
            this(getInternalClient(null));
        }

        public Factory(OkHttpClient okHttpClient) {
            this(getInternalClient(okHttpClient));
        }

        public Factory(okhttp3.Call.Factory client) {
            this.client = client;
        }

        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpUrlLoader(this.client);
        }

        public void teardown() {
        }
    }
}
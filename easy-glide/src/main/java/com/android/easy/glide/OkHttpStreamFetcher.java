package com.android.easy.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.HttpException;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class OkHttpStreamFetcher implements DataFetcher<InputStream> {
    private final Call.Factory client;
    private final GlideUrl url;
    public InputStream stream;
    public ResponseBody responseBody;
    private volatile Call call;

    public OkHttpStreamFetcher(Call.Factory client, GlideUrl url) {
        this.client = client;
        this.url = url;
    }


    @Override
    public void loadData(@NonNull Priority priority, @NonNull final DataCallback<? super InputStream> callback) {
        Request.Builder requestBuilder = (new Request.Builder()).url(url.toStringUrl());
        Iterator request = url.getHeaders().entrySet().iterator();

        while (request.hasNext()) {
            Map.Entry headerEntry = (Map.Entry) request.next();
            String key = (String) headerEntry.getKey();
            requestBuilder.addHeader(key, (String) headerEntry.getValue());
        }

        Request request1 = requestBuilder.build();
        call = client.newCall(request1);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                callback.onLoadFailed(e);
            }

            public void onResponse(Call call, Response response) throws IOException {
                responseBody = response.body();
                if (response.isSuccessful()) {
                    long contentLength = responseBody.contentLength();
                    OnGlideProgressListener listener = GlideProgressManager.getInstance().getListener(url.toStringUrl());
                    stream = ContentLengthInputStream.obtain(responseBody.byteStream(), contentLength, listener);
                    callback.onDataReady(stream);
                } else {
                    callback.onLoadFailed(new HttpException(response.message(), response.code()));
                }
            }
        });
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
            GlideProgressManager.getInstance().removeListener(url.toStringUrl());
        } catch (IOException var2) {
            ;
        }

        if (responseBody != null) {
            responseBody.close();
        }
    }

    @Override
    public void cancel() {
        Call local = call;
        if (local != null) {
            local.cancel();
        }
        GlideProgressManager.getInstance().removeListener(url.toStringUrl());
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
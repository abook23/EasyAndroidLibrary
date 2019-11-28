package com.android.easy.retrofit.progress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by abook23 on 2016/11/22.
 * Versions 1.0
 * 下载
 */

public class ProgressResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private OnDownloadListener mListener;
    private BufferedSource mBufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, OnDownloadListener progressListener) {
        mResponseBody = responseBody;
        mListener = progressListener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            //包装
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1
                mListener.onProgress(totalBytesRead, contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}

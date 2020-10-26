package com.android.easy.glide;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

//拷贝 Glide 的方法 ContentLengthInputStream
public final class ContentLengthInputStream extends FilterInputStream {
    private static final String TAG = "ContentLengthStream";
    private static final int UNKNOWN = -1;

    private final long contentLength;
    private final OnGlideProgressListener onGlideProgressListener;
    private int readSoFar;

    @NonNull
    public static InputStream obtain(
            @NonNull InputStream other, @Nullable String contentLengthHeader, OnGlideProgressListener onGlideProgressListener) {
        return obtain(other, parseContentLength(contentLengthHeader), onGlideProgressListener);
    }

    @NonNull
    public static InputStream obtain(@NonNull InputStream other, long contentLength, OnGlideProgressListener onGlideProgressListener) {
        return new ContentLengthInputStream(other, contentLength, onGlideProgressListener);
    }

    private static int parseContentLength(@Nullable String contentLengthHeader) {
        int result = UNKNOWN;
        if (!TextUtils.isEmpty(contentLengthHeader)) {
            try {
                result = Integer.parseInt(contentLengthHeader);
            } catch (NumberFormatException e) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "failed to parse content length header: " + contentLengthHeader, e);
                }
            }
        }
        return result;
    }

    private ContentLengthInputStream(@NonNull InputStream in, long contentLength, OnGlideProgressListener onGlideProgressListener) {
        super(in);
        this.contentLength = contentLength;
        this.onGlideProgressListener = onGlideProgressListener;
        if (onGlideProgressListener != null)
            onGlideProgressListener.onStart(contentLength);
    }

    @Override
    public synchronized int available() throws IOException {
        return (int) Math.max(contentLength - readSoFar, in.available());
    }

    @Override
    public synchronized int read() throws IOException {
        int value = super.read();
        checkReadSoFarOrThrow(value >= 0 ? 1 : -1);
        return value;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0 /*byteOffset*/, buffer.length /*byteCount*/);
    }

    @Override
    public synchronized int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return checkReadSoFarOrThrow(super.read(buffer, byteOffset, byteCount));
    }

    private int checkReadSoFarOrThrow(int read) throws IOException {
        if (read >= 0) {
            readSoFar += read;
            if (onGlideProgressListener != null){
                onGlideProgressListener.onProgress(readSoFar, contentLength);
                if (readSoFar == contentLength) {
                    onGlideProgressListener.onComplete();
                }
            }
        } else if (contentLength - readSoFar > 0) {
            throw new IOException(
                    "Failed to read all expected data"
                            + ", expected: "
                            + contentLength
                            + ", but read: "
                            + readSoFar);
        }
        return read;
    }
}
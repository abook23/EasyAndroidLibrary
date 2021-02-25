package com.android.easy.glide;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * author abook23@163.com
 * 2019/12/09
 */
public class RoundedCornersFitStart extends BitmapTransformation {
    private static final String ID = "com.abook23.tv.util.RoundedCornersFitStart";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final int roundingRadius;

    public RoundedCornersFitStart(int roundingRadius) {
        this.roundingRadius = roundingRadius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap cropBitmap = Bitmap.createBitmap(toTransform, 0, 0, outWidth, outHeight, null, false);
        return TransformationUtils.roundedCorners(pool, cropBitmap, roundingRadius);
    }


    //避免闪烁 equals hashCode updateDiskCacheKey

    @Override
    public boolean equals(Object o) {
        return o instanceof RoundedCornersFitStart;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}

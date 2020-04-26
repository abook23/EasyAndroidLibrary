package com.abook23.tv.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

/**
 * @author abook23@163.com
 *  2019/12/09
 */
public class RoundedCornersFitStart extends BitmapTransformation {
    private int roundingRadius;

    public RoundedCornersFitStart(int roundingRadius) {
        this.roundingRadius = roundingRadius;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap cropBitmap = Bitmap.createBitmap(toTransform, 0, 0, outWidth, outHeight, null, false);
        return TransformationUtils.roundedCorners(pool, cropBitmap, roundingRadius);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }
}

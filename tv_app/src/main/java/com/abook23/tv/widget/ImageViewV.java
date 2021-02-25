package com.abook23.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * author abook23@163.com
 *  2019/12/04
 */
public class ImageViewV extends AppCompatImageView {
    public ImageViewV(Context context) {
        super(context);
    }

    public ImageViewV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageViewV(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = resolveSize(0, widthMeasureSpec);
        int height = resolveSize(width * 4 / 3, heightMeasureSpec);
//        setMeasuredDimension(width,height);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

package com.abook23.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author abook23@163.com
 * @date 2020/04/30
 */
public class FrameLayoutH extends FrameLayout {
    public FrameLayoutH(@NonNull Context context) {
        super(context);
    }

    public FrameLayoutH(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameLayoutH(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FrameLayoutH(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
        int width = getMeasuredWidth();
//        int height = getMeasuredHeight();
        int height = resolveSize(width, heightMeasureSpec);
        //高度和宽度一样
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

package com.abook23.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * author abook23@163.com
 *  2019/12/06
 */
public class SurfaceViewH extends SurfaceView {
    public SurfaceViewH(Context context) {
        super(context);
    }

    public SurfaceViewH(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SurfaceViewH(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = resolveSize(0, heightMeasureSpec);
        int width = resolveSize(height * 16 / 9, widthMeasureSpec);
//        setMeasuredDimension(width,height);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

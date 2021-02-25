package com.android.easy.mediastore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2021/2/25 15:25
 * updateUser:     更新者：
 * updateDate:     2021/2/25 15:25
 * updateRemark:   更新说明：
 * version:        1.0
 */
public class PhotoViewPager extends ViewPager {
    public PhotoViewPager(@NonNull Context context) {
        super(context);
    }

    public PhotoViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}

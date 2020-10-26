package com.android.easy.mediastore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * @author abook23@163.com
 * @date 2020/05/08
 */
public class TextViewMarquee extends TextView {
    public TextViewMarquee(Context context) {
        super(context);
    }

    public TextViewMarquee(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextViewMarquee(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}

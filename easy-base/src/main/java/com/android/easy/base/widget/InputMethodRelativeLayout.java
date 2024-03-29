package com.android.easy.base.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

/**
 * author abook23
 *         自定义布局解决键盘弹出挡住输入框的问题
 *         请使用 KeyboardListenRelativeLayout
 */
@Deprecated
public class InputMethodRelativeLayout extends RelativeLayout {
    private int width;
    protected OnSizeChangedListenner onSizeChangedListenner;
    private boolean sizeChanged = false; //变化的标志
    private int height;
    private int screenWidth; //屏幕宽度
    private int screenHeight; //屏幕高度

    public InputMethodRelativeLayout(Context context,
                                     AttributeSet paramAttributeSet) {
        super(context, paramAttributeSet);
//		Display localDisplay = ((Activity) context).getWindowManager()
//				.getDefaultDisplay();
//		this.screenWidth = localDisplay.getWidth() ;
//		this.screenHeight = localDisplay.getHeight();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.screenWidth = dm.widthPixels;
        this.screenHeight = dm.heightPixels;
    }

    public InputMethodRelativeLayout(Context paramContext,
                                     AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.width = widthMeasureSpec;
        this.height = heightMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw,
                              int oldh) {
        //监听不为空、宽度不变、当前高度与历史高度不为0
        if ((this.onSizeChangedListenner != null) && (w == oldw) && (oldw != 0)
                && (oldh != 0)) {
            if ((h >= oldh)
                    || (Math.abs(h - oldh) <= 1 * this.screenHeight / 4)) {
                if ((h <= oldh)
                        || (Math.abs(h - oldh) <= 1 * this.screenHeight / 4))
                    return;
                this.sizeChanged = false;
            } else {
                this.sizeChanged = true;
            }
            this.onSizeChangedListenner.onSizeChange(this.sizeChanged, oldh, h);
            measure(this.width - w + getWidth(), this.height - h + getHeight());
        }
    }

    /**
     * 设置监听事件
     *
     * @param paramonSizeChangedListenner
     */
    public void setOnSizeChangedListenner(
            OnSizeChangedListenner paramonSizeChangedListenner) {
        this.onSizeChangedListenner = paramonSizeChangedListenner;
    }

    /**
     * 大小改变的内部接口
     *
     * author junjun
     */
    public interface OnSizeChangedListenner {
        void onSizeChange(boolean paramBoolean, int w, int h);
    }
}

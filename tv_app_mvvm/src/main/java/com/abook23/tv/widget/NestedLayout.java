package com.abook23.tv.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;

/**
 * author abook23@163.com
 * 2019/12/19
 */
public class NestedLayout extends LinearLayout implements NestedScrollingParent2 {
    int topHeight = 0;
    View topView;
    View nestedScrollView;

    public NestedLayout(Context context) {
        this(context, null);
    }

    public NestedLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        topHeight = topView.getMeasuredHeight();

        ViewGroup.LayoutParams layoutParams = nestedScrollView.getLayoutParams();
        layoutParams.height = h - getPaddingTop() - getPaddingBottom() - 20;
        nestedScrollView.setLayoutParams(layoutParams);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        topView = getChildAt(0);
        nestedScrollView = getChildAt(getChildCount() - 1);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return true;//true 表明父容器接受嵌套滚动,如果为false 则其他方法将不会调用
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        boolean hiddenTop = dy > 0 && getScrollY() < topHeight;
        boolean showTop = dy < 0 && getScrollY() > 0 && !ViewCompat.canScrollVertically(target, -1);
        if (hiddenTop || showTop) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {

    }

    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > topHeight) {
            y = topHeight;
        }
        if (y != getScrollY()) {
            super.scrollTo(x, y);
        }
    }

//    @Override
//    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
//        if (getScrollY()>=topHeight){
//            return false;
//        }
//        recyclerView.dispatchNestedPreFling(getScrollX(),getScrollY());
//        return super.onNestedPreFling(target, velocityX, velocityY);
//    }
}

package com.android.easy.mediastore.widget;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * 实现ViewPager左右滑动时的时差
 * Created by xmuSistone on 2016/9/18.
 */
public class CustPagerTransformer implements ViewPager.PageTransformer {

    private int maxTranslateOffsetX;
    private ViewPager viewPager;

    public CustPagerTransformer(Context context) {
        this.maxTranslateOffsetX = dp2px(context, 180);
    }

    private static final float MIN_SCALE = 0.90f;
    private static final float MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(final View view, float position) {

//        if (viewPager == null) {
//            viewPager = (ViewPager) view.getParent();
//        }
//        int leftInScreen = view.getLeft() - viewPager.getScrollX();
//        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
//        int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
//        float offsetRate = (float) offsetX * 0.38f / viewPager.getMeasuredWidth();
//        float scaleFactor = 1 - Math.abs(offsetRate);
//        if (scaleFactor > 0) {
//            view.setScaleX(scaleFactor);
//            view.setScaleY(scaleFactor);
//            view.setTranslationX(-maxTranslateOffsetX * offsetRate);
//        }

        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        Log.e("TAG", view + " , " + position + "");
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 1) { //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }
            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

    /**
     * dp和像素转换
     */
    private int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

}

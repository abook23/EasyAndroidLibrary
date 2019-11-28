package com.android.easy.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * @author abook23@163.com
 * @date 2019/11/28
 */
public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);//无标题栏
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isMatchParent()) {
            // 设置宽度为屏宽, 靠近屏幕底部。
            Window window = getDialog().getWindow();
            // 一定要设置Background，如果不设置，window属性设置无效
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = getGravity();
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    /**
     * 是否全面屏显示
     *
     * @return
     */
    public boolean isMatchParent() {
        return false;
    }

    /**
     * 显示位置
     *
     * @return
     */
    public int getGravity() {
        return Gravity.CENTER;
    }
}

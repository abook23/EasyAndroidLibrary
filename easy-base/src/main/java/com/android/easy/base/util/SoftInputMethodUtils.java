package com.android.easy.base.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.android.easy.base.listener.OnInputMethodListener;

import static android.content.Context.INPUT_METHOD_SERVICE;


/**
 * Created by abook23 on 2017/4/12.
 */

public class SoftInputMethodUtils {
    private static int oldHeight = -1;

    public void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
    /**
     * 隐藏键盘
     */
    public static void hideSoftInput(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     */
    public static void openSoftInput(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static void openSoftInput(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    public static void inputMethodListener(Activity activity, final OnInputMethodListener listener) {
        oldHeight = -1;
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        decorView.getWindowVisibleDisplayFrame(rect);
                        int screenHeight = decorView.getRootView().getHeight();
                        int heightDifference = screenHeight - rect.bottom;
                        if (oldHeight != heightDifference)
                            listener.onInputMethodListener(heightDifference);
                        oldHeight = heightDifference;
                    }
                }
        );
    }
}

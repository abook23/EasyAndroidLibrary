package com.android.easy.base.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast;

    public static void show(Context context, String msg) {
        makeText(context, msg);
    }

    public static void show(Context context, int msg) {
        makeText(context, msg);
    }

    public static void debugShow(Context context, String text) {
        if (L.allow)
            makeText(context, text);
    }

    private static void makeText(Context context, Object text) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(context, String.valueOf(text), Toast.LENGTH_SHORT);
        toast.show();
    }
}

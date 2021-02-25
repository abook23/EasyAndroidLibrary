package com.android.easy.base.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(new Date());
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(long dataTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(dataTime);
    }

    /**
     * @param dateStr
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String format(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(dateStr);
    }

    /**
     * @param dateTime
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMDDate(long dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(dateTime);
    }

    /**
     * @param dateType d
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String dateType) {
        SimpleDateFormat format = new SimpleDateFormat(dateType);
        return format.format(new Date());
    }

    /**
     * @param dateType d
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String dateType, long dataTime) {
        SimpleDateFormat format = new SimpleDateFormat(dateType);
        return format.format(dataTime);
    }


    public static String formatterTime(long currentPosition) {
        SimpleDateFormat dateFormat;
        if (currentPosition > 1000 * 60 * 60) {
            dateFormat = new SimpleDateFormat("hh:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        } else {
            dateFormat = new SimpleDateFormat("mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        }
        return dateFormat.format(new Date(currentPosition));
    }

}

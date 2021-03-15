package com.android.easy.mediastore.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.android.easy.mediastore.MediaStoreConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * author abook23@163.com
 * 2019/11/28
 */
public class LocalMediaLoader implements Handler.Callback {
    private static final int MSG_QUERY_MEDIA_SUCCESS = 0;
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private static final String NOT_GIF = "!='image/gif'";
    /**
     * 过滤掉小于500毫秒的录音
     */
    private static final int AUDIO_DURATION = 500;
    private Context mContext;
    private boolean isAndroidQ;
    private MediaStoreConfig config;
    private Handler mHandler;
    /**
     * unit
     */
    private static final long FILE_SIZE_UNIT = 1024 * 1024L;
    /**
     * 媒体文件数据库字段
     */
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DURATION,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME};

    /**
     * 图片
     */
    private static final String SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_NOT_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF;
    /**
     * 查询指定后缀名的图片
     */
    private static final String SELECTION_SPECIFIED_FORMAT = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE;

    /**
     * 查询条件(音视频)
     *
     * @param time_condition
     * @return
     */
    private static String getSelectionArgsForSingleMediaCondition(String time_condition) {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                + " AND " + time_condition;
    }

    /**
     * 查询(视频)
     *
     * @return
     */
    private static String getSelectionArgsForSingleMediaCondition() {
        return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    }

    /**
     * 全部模式下条件
     *
     * @param time_condition
     * @param isGif
     * @return
     */
    private static String getSelectionArgsForAllMediaCondition(String time_condition, boolean isGif) {
        String condition = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                + (isGif ? "" : " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
                + " OR "
                + (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? AND " + time_condition) + ")"
                + " AND " + MediaStore.MediaColumns.SIZE + ">0";
        return condition;
    }

    /**
     * 获取图片or视频
     */
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

    /**
     * 获取指定类型的文件
     *
     * @param mediaType
     * @return
     */
    private static String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }


    public LocalMediaLoader(Context context, MediaStoreConfig config) {
        this.mContext = context.getApplicationContext();
        this.isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
        this.config = config;
        this.mHandler = new Handler(Looper.getMainLooper(), this);
    }

    public void loadAllMedia() {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Cursor data = mContext.getContentResolver().query(QUERY_URI, PROJECTION, getSelection(), getSelectionArgs(), ORDER_BY);
                try {
                    List<LocalMediaFolder> imageFolders = new ArrayList<>();
//                    LocalMediaFolder allImageFolder = new LocalMediaFolder();
//                    List<LocalMedia> latelyImages = new ArrayList<>();
                    if (data != null) {
                        int count = data.getCount();
                        if (count > 0) {
                            data.moveToFirst();
                            do {
                                long id = data.getLong(data.getColumnIndexOrThrow(PROJECTION[0]));
//                                String path = isAndroidQ ? getRealPathAndroid_Q(id) : data.getString
//                                        (data.getColumnIndexOrThrow(PROJECTION[1]));
                                String path = data.getString(data.getColumnIndexOrThrow(PROJECTION[1]));
                                String mimeType = data.getString(data.getColumnIndexOrThrow(PROJECTION[2]));
                                int w = data.getInt(data.getColumnIndexOrThrow(PROJECTION[3]));
                                int h = data.getInt(data.getColumnIndexOrThrow(PROJECTION[4]));
                                long duration = data.getLong(data.getColumnIndexOrThrow(PROJECTION[5]));
                                long size = data.getLong(data.getColumnIndexOrThrow(PROJECTION[6]));
                                String folderName = data.getString(data.getColumnIndexOrThrow(PROJECTION[7]));

                                if (config.filterFileSize > 0) {
                                    if (size > config.filterFileSize * FILE_SIZE_UNIT) {
                                        continue;
                                    }
                                }

                                if (mimeType.startsWith(MediaStoreConfig.MIME_TYPE_PREFIX_VIDEO)) {
                                    if (duration == 0) {
                                        duration = MediaUtils.extractDuration(mContext, isAndroidQ, path);
                                    }
                                    if (w == 0 && h == 0) {
                                        int[] newSize = isAndroidQ ? MediaUtils
                                                .getLocalVideoWidthOrHeightToAndroidQ(mContext, path)
                                                : MediaUtils.getLocalVideoWidthOrHeight(path);
                                        w = newSize[0];
                                        h = newSize[1];
                                    }
                                    if (config.videoMinSecond > 0 && duration < config.videoMinSecond) {
                                        // 如果设置了最小显示多少秒的视频
                                        continue;
                                    }
                                    if (config.videoMaxSecond > 0 && duration > config.videoMaxSecond) {
                                        // 如果设置了最大显示多少秒的视频
                                        continue;
                                    }
                                    if (duration == 0) {
                                        // 时长如果为0，就当做损坏的视频处理过滤掉
                                        continue;
                                    }
                                    if (size <= 0) {
                                        // 视频大小为0过滤掉
                                        continue;
                                    }
                                }

                                LocalMedia image = new LocalMedia(path, duration, config.mediaMode, mimeType, w, h, size);
                                LocalMediaFolder folder = getImageFolder(path, folderName, imageFolders);
                                List<LocalMedia> images = folder.getImages();
                                images.add(image);
                                folder.setImageNum(folder.getImageNum() + 1);
//                                latelyImages.add(image);
//                                int imageNum = allImageFolder.getImageNum();
//                                allImageFolder.setImageNum(imageNum + 1);

                            } while (data.moveToNext());
                            sortFolder(imageFolders);
//                            if (latelyImages.size() > 0) {
//                                imageFolders.add(0, allImageFolder);
//                                allImageFolder.setFirstImagePath(latelyImages.get(0).getPath());
//                                String title = config.mediaMode == MediaMode.TYPE_AUDIO ? "音频" : "照片";
//                                allImageFolder.setName(title);
//                                allImageFolder.setImages(latelyImages);
//                            }
                        }
                        // 线程切换
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_QUERY_MEDIA_SUCCESS, imageFolders));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getSelection() {
        switch (config.mediaMode) {
            case TYPE_IMAGE_VIDEO:
                // 获取 图片 和 视频
                return getSelectionArgsForAllMediaCondition(getDurationCondition(0, 0), config.isGif);
            case TYPE_IMAGE:
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定的类型的图片
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
                }
                return config.isGif ? SELECTION : SELECTION_NOT_GIF;
            case TYPE_VIDEO:
                // 获取视频
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定的类型的图片
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
                }
                return getSelectionArgsForSingleMediaCondition();
            case TYPE_AUDIO:
                // 获取音频
                if (!TextUtils.isEmpty(config.specifiedFormat)) {
                    // 获取指定的类型的图片
                    return SELECTION_SPECIFIED_FORMAT + "='" + config.specifiedFormat + "'";
                }
                return getSelectionArgsForSingleMediaCondition(getDurationCondition(0, AUDIO_DURATION));
        }
        return null;
    }

    private String[] getSelectionArgs() {
        switch (config.mediaMode) {
            case TYPE_IMAGE_VIDEO:
                return SELECTION_ALL_ARGS;
            case TYPE_IMAGE:
                // 只获取图片
                String[] MEDIA_TYPE_IMAGE = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                return MEDIA_TYPE_IMAGE;
            case TYPE_VIDEO:
                // 只获取视频
                return getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            case TYPE_AUDIO:
                String[] MEDIA_TYPE_AUDIO = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO);
                return MEDIA_TYPE_AUDIO;
        }
        return null;
    }

    /**
     * 文件夹数量进行排序
     *
     * @param imageFolders
     */
    private void sortFolder(List<LocalMediaFolder> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<LocalMediaFolder>() {
            @Override
            public int compare(LocalMediaFolder lhs, LocalMediaFolder rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    /**
     * 适配Android Q
     *
     * @param id
     * @return
     */
    private String getRealPathAndroid_Q(long id) {
        return QUERY_URI.buildUpon().appendPath(String.valueOf(id)).build().toString();
    }

    /**
     * 创建相应文件夹
     *
     * @param path
     * @param imageFolders
     * @param folderName
     * @return
     */
    private LocalMediaFolder getImageFolder(String path, String folderName, List<LocalMediaFolder> imageFolders) {
        for (LocalMediaFolder folder : imageFolders) {
            // 同一个文件夹下，返回自己，否则创建新文件夹
            if (folder.getName().equals(folderName)) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderName);
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    /**
     * 获取视频(最长或最小时间)
     *
     * @param exMaxLimit
     * @param exMinLimit
     * @return
     */
    private String getDurationCondition(long exMaxLimit, long exMinLimit) {
        long maxS = config.videoMaxSecond == 0 ? Long.MAX_VALUE : config.videoMaxSecond;
        if (exMaxLimit != 0) {
            maxS = Math.min(maxS, exMaxLimit);
        }
        return String.format(Locale.CHINA, "%d <%s " + MediaStore.MediaColumns.DURATION + " and " + MediaStore.MediaColumns.DURATION + " <= %d",
                Math.max(exMinLimit, config.videoMinSecond),
                Math.max(exMinLimit, config.videoMinSecond) == 0 ? "" : "=",
                maxS);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (mCompleteListener == null) return false;
        switch (msg.what) {
            case MSG_QUERY_MEDIA_SUCCESS:
                mCompleteListener.loadComplete((List<LocalMediaFolder>) msg.obj);
                break;
        }
        return false;
    }

    private LocalMediaLoadListener mCompleteListener;

    public void setCompleteListener(LocalMediaLoadListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<LocalMediaFolder> folders);
    }
}


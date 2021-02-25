package com.android.easy.mediastore;

import com.android.easy.mediastore.utils.MediaMode;

/**
 * author abook23@163.com
 * date 2020/04/30
 */
public class MediaStoreConfig {

    public final static String MIME_TYPE_IMAGE = "image/jpeg";
    public final static String MIME_TYPE_VIDEO = "video/mp4";
    public final static String MIME_TYPE_AUDIO = "audio/mpeg";

    public final static String MIME_TYPE_PREFIX_IMAGE = "image";
    public final static String MIME_TYPE_PREFIX_VIDEO = "video";
    public final static String MIME_TYPE_PREFIX_AUDIO = "audio";

    public boolean isGif = true;

    public MediaMode mediaMode = MediaMode.TYPE_IMAGE_VIDEO;

    public int filterFileSize;//文件大小

    public int videoMinSecond;//最小视频
    public int videoMaxSecond;//最大视频

    public String specifiedFormat;//文件类型

    public int selectMaxCount = 9;


}

package com.android.easy.play;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description: 视频文件下载
 * Author: yangxiong
 * E-mail: abook23@163.com
 * CreateDate: 2020/8/4 9:22
 * UpdateUser: 更新者：
 * UpdateDate: 2020/8/4 9:22
 * UpdateRemark: 更新说明：支持 m3u8文件下线 和 普通文件断点下载
 * Version: 1.0
 */
public class DownloadVideoManager {

    private static final String TYPE_M3U8 = ".m3u8";
    private static final String TYPE_COMPLETE = "Complete";

    private ExecutorService executorService;
    private ConcurrentHashMap<String, HttpURLConnection> downloadConnection = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> downloadURL = new ConcurrentHashMap<>();//主要针对 m3u8 文件
    private static DownloadVideoManager sDownloadVideoManager = new DownloadVideoManager();

    public final static String VIDEO_CACHE_SHARED_PREFERENCES = "video_cache_shared_preferences";
    private SharedPreferences mSharedPreferences;

    public static DownloadVideoManager getInstance() {
        return sDownloadVideoManager;
    }


    public void newInstanceExecutorService() {
//        executorService = Executors.newSingleThreadExecutor();
        executorService = Executors.newFixedThreadPool(8);
    }

    private DownloadVideoManager() {
        newInstanceExecutorService();
    }

    private SharedPreferences getSharedPreferences(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(VIDEO_CACHE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    /**
     * 文件缓存 地址
     *
     * @param context
     * @param url
     * @return
     */
    public static String getCacheLocalPath(Context context, String url) {
        return getCacheLocalDir(context) + File.separator + getMD5(url);
    }

    /**
     * 删除视频缓存
     * @param context
     * @param url
     */
    public static void delCacheVideo(Context context, String url) {
        getInstance().getSharedPreferences(context).edit().remove(url).apply();
        getInstance().deleteDirectory(new File(getCacheLocalPath(context, url)));
    }

    /**
     * 删除全部视频缓存
     * @param context
     * @return
     */
    public static String clearCacheAll(Context context) {
        getInstance().getSharedPreferences(context).edit().clear().apply();
        long size = getInstance().deleteDirectory(new File(getCacheLocalDir(context)));
        return Formatter.formatFileSize(context, size);
    }


    /**
     * 文件缓存位置
     *
     * @param context
     * @return
     */
    public static String getCacheLocalDir(Context context) {
        if (!Environment.isExternalStorageRemovable() || Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath();
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    private long deleteDirectory(File directory) {
        return countDirFileSize(directory, true);
    }

    private long countDirFileSize(File file, boolean del) {
        long fileSize = 0;
        if (file.isDirectory()) {
            File[] sub_files = file.listFiles();
            for (File sub_file : sub_files) {
                long length = countDirFileSize(sub_file, del);
                fileSize += length;
            }
        } else {
            fileSize += file.length();
            if (del && file.isFile() && file.exists()) {
                file.delete();
            }
        }
        return fileSize;
    }

    /**
     * 文件缓存播放地址
     *
     * @param context
     * @param url
     * @return
     */
    public static String getCacheLocalPlayPath(Context context, String url) {
        String fileNam = url.substring(url.lastIndexOf("/") + 1);
        return getCacheLocalPath(context, url) + File.separator + fileNam;
    }

    public void downloadFile(Context context, String url, Call call) {
        String savePath = getCacheLocalPath(context, url);
        mSharedPreferences = getSharedPreferences(context);
        if (downloadURL.get(url) != null && downloadURL.get(url)) {
            return;
        }
        if (url.endsWith(TYPE_M3U8)) {
            downloadM3U8(savePath, url, call);
        } else {
            downloadFile(savePath, url, call);
        }
    }


    private void downloadFile(String savePath, String url, Call call) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    call.onStart(new File(savePath), 0);
                    downloadURL.put(url, true);
                    long start = mSharedPreferences.getLong(url, 0);
                    load(url, new File(savePath), start, new Call() {
                        @Override
                        public void onStart(File file, long max) {

                        }

                        @Override
                        public void onProgress(long progress, long max) {
                            call.onProgress(progress, -1);
                            mSharedPreferences.edit().putLong(url, progress).apply();
                        }

                        @Override
                        public void onComplete(File file) {
                            mSharedPreferences.edit().putString(url, TYPE_COMPLETE).apply();
                        }
                    });
                    call.onComplete(new File(savePath));
                    downloadURL.put(url, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isComplete(String url) {
        return TYPE_COMPLETE.equals(mSharedPreferences.getString(url, "0000"));
    }


    /**
     * @param savePath
     * @param url
     * @param call
     */
    private void downloadM3U8(String savePath, String url, Call call) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadURL.put(url, true);
                    String basePath = url.substring(0, url.lastIndexOf("/") + 1);
                    File file = saveIndexM3u8uFile(url, new File(savePath));
                    String m3u8Url = getM3u8URL(file);

                    String m3u8UrlPath = m3u8Url.substring(0, m3u8Url.lastIndexOf("/") + 1);
                    File tsFile = saveIndexM3u8uFile(basePath + m3u8Url, new File(savePath, m3u8UrlPath));
                    List<String> tsList = getM3u8urlTS(tsFile);

                    call.onStart(file, tsList.size());

                    String tsBaseURL = basePath + m3u8UrlPath;
                    int start = mSharedPreferences.getInt(url, 0);
                    for (int i = start; i < tsList.size(); i++) {
                        load(tsBaseURL + tsList.get(i), new File(savePath, m3u8UrlPath));
                        mSharedPreferences.edit().putInt(url, i + 1).apply();
                        if (downloadURL.get(url) == null || !downloadURL.get(url)) {
                            return;
                        }
                        call.onProgress(i + 1, tsList.size());
                    }
                    call.onComplete(new File(savePath));
                    mSharedPreferences.edit().putString(url, TYPE_COMPLETE).apply();
                    downloadURL.put(url, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop(String url) {
        downloadURL.put(url, false);
        HttpURLConnection connection = downloadConnection.get(url);
        try {
            if (connection != null)
                connection.disconnect();
        } catch (Exception ignored) {
        }
    }

    public void shutdownNow() {
        downloadURL.clear();
        for (Map.Entry<String, HttpURLConnection> entry : downloadConnection.entrySet()) {
            HttpURLConnection connection = entry.getValue();
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception ignored) {
            }
        }
        downloadConnection.clear();
        executorService.shutdown();
        if (executorService.isShutdown()) {
            newInstanceExecutorService();
        }
    }

    private File saveIndexM3u8uFile(String url, File savePath) throws IOException {
        File file = null;
        HttpURLConnection connection = requestHttp(url);
        if (connection.getResponseCode() == 200) {
            file = saveFile(connection.getInputStream(), savePath, "index.m3u8");
        }
        connection.disconnect();
        return file;
    }

    private String getM3u8URL(File file) throws IOException {
        String m3u8PUrl = null;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith(TYPE_M3U8)) {
                m3u8PUrl = line;
            }
        }
        reader.close(); // 关闭流
        return m3u8PUrl;
    }

    private List<String> getM3u8urlTS(File file) throws IOException {
        List<String> tsList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) { // 循环从流中读取
            if (line.endsWith(".ts")) {
                tsList.add(line);
            }
        }
        reader.close(); // 关闭流
        return tsList;
    }

    private void load(String url, File savePath) {
        HttpURLConnection connection = null;
        try {
            String fileNam = url.substring(url.lastIndexOf("/") + 1);
            connection = requestHttp(url);
            downloadConnection.put(url, connection);
            saveFile(connection.getInputStream(), savePath, fileNam);
            connection.disconnect();
            downloadConnection.remove(url);
        } catch (IOException e) {
            e.printStackTrace();
            if (connection != null)
                connection.disconnect();
        }
    }

    private void load(String url, File savePath, long start, Call call) {
        HttpURLConnection connection = null;
        try {
            String fileNam = url.substring(url.lastIndexOf("/") + 1);
            connection = requestHttp(url, start);
            downloadConnection.put(url, connection);
            long max = getContentLengthLong(connection, "content-length", -1);
            call.onStart(savePath, max);
            saveFile(connection.getInputStream(), savePath, fileNam, start, call);
            call.onComplete(savePath);
            connection.disconnect();
            downloadConnection.remove(url);
        } catch (IOException e) {
            e.printStackTrace();
            if (connection != null)
                connection.disconnect();
        }
    }

    public long getContentLengthLong(HttpURLConnection connection, String name, long Default) {
        String value = connection.getHeaderField(name);
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
        }
        return Default;
    }

    private File saveFile(InputStream input, File parent, String fileName) throws IOException {
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File file = new File(parent, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream output = new FileOutputStream(file);
        int len;
        byte[] buffer = new byte[4 * 1024];
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
        }
        output.flush();
        input.close();
        return file;
    }

    private File saveFile(InputStream input, File parent, String fileName, long start, Call call) throws IOException {
        if (!parent.exists()) {
            parent.mkdirs();
        }
        File file = new File(parent, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        //断点文件操作
        RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
        accessFile.seek(start);

        int len;
        byte[] buffer = new byte[4 * 1024];
        while ((len = input.read(buffer)) != -1) {
            accessFile.write(buffer, 0, len);

            start = start + len;
            call.onProgress(start, -1);
        }
        accessFile.close();

        input.close();
        return file;
    }

    private HttpURLConnection requestHttp(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection;
    }

    /**
     * 断点下载
     *
     * @param url
     * @param start
     * @return
     * @throws IOException
     */
    private HttpURLConnection requestHttp(String url, long start) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Range", "bytes=" + start + "-");
        connection.connect();
        return connection;
    }

    public static String getMD5(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");// 获取一个实例，并传入加密方式
            digest.reset();// 清空一下
            digest.update(content.getBytes());// 写入内容,可以指定编码方式content.getBytes("utf-8");
            StringBuilder builder = new StringBuilder();
            for (byte b : digest.digest()) {
                builder.append(Integer.toHexString((b >> 4) & 0xf));
                builder.append(Integer.toHexString(b & 0xf));
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Call {
        void onStart(File file, long max);

        void onProgress(long progress, long max);

        void onComplete(File file);
    }
}

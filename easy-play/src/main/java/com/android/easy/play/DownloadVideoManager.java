package com.android.easy.play;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
 * @Description: 描述
 * @Author: yangxiong
 * @E-mail: abook23@163.com
 * @CreateDate: 2020/8/4 9:22
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/8/4 9:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class DownloadVideoManager {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    //    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private ConcurrentHashMap<String, Boolean> downloadMap = new ConcurrentHashMap<>();
    private static DownloadVideoManager sDownloadVideoManager = new DownloadVideoManager();

    public final static String VIDEO_CACHE_SHARED_PREFERENCES = "video_cache_shared_preferences";
    private SharedPreferences mSharedPreferences;

    public static DownloadVideoManager getInstance() {
        return sDownloadVideoManager;
    }

    private SharedPreferences getSharedPreferences(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(VIDEO_CACHE_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    public static String getCacheLocalPath(Context context, String url) {
//        return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getPath() + File.separator + Utils.getMD5(url);
        return context.getExternalCacheDir().getPath() + File.separator + getMD5(url);
    }

    public static String getCacheLocalPlayPath(Context context, String url) {
        return getCacheLocalPath(context, url) + "/index.m3u8";
    }

    public void downloadM3U8(Context context, String url, Call call) {
        String savePath = getCacheLocalPath(context, url);
        mSharedPreferences = getSharedPreferences(context);
        downloadM3U8(savePath, url, call);
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
                downloadMap.put(url, true);
                String basePath = url.substring(0, url.lastIndexOf("/") + 1);
                try {
                    File file = saveIndexM3u8uFile(url, new File(savePath));
                    String m3u8Url = getM3U8URL(file);

                    String m3u8UrlPath = m3u8Url.substring(0, m3u8Url.lastIndexOf("/") + 1);
                    File tsFile = saveIndexM3u8uFile(basePath + m3u8Url, new File(savePath, m3u8UrlPath));
                    List<String> tsList = getM3u8urlTS(tsFile);

                    call.onStart(file, tsList.size());

                    String tsBaseURL = basePath + m3u8UrlPath;
                    int start = mSharedPreferences.getInt(url, 0);
                    for (int i = start; i < tsList.size(); i++) {
                        load(tsBaseURL + tsList.get(i), new File(savePath, m3u8UrlPath));
                        mSharedPreferences.edit().putInt(url, i + 1).apply();
                        if (!downloadMap.get(url)) {
                            return;
                        }
                        call.onProgress(i + 1, tsList.size());
                    }
                    call.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop(String url) {
        downloadMap.put(url, false);
    }

    public void shutdownNow() {
        for (Map.Entry<String, Boolean> entry : downloadMap.entrySet()) {
            entry.setValue(false);
        }
//        executorService.shutdownNow();
//        executorService = Executors.newSingleThreadExecutor();
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

    private String getM3U8URL(File file) throws IOException {
        String m3u8PUrl = null;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.endsWith(".m3u8")) {
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

    private void load(String url, File savePath) throws IOException {
        String fileNam = url.substring(url.lastIndexOf("/") + 1);
        HttpURLConnection connection = requestHttp(url);
        saveFile(connection.getInputStream(), savePath, fileNam);
        connection.disconnect();
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

    private HttpURLConnection requestHttp(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
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

        void onProgress(int progress, long max);

        void onComplete();
    }
}

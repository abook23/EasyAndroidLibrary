package com.android.easy.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * author abook23
 * 2014/9/22
 * /cache_data/[packageName]
 */
public class FileUtils2 {

    private Context mContext;
    private static FileUtils2 sFileUtils2;
    /**
     * 软缓存目录名
     */
    private String SD_FOLDER_NAME = "/Android/data";


    /**
     * @param context context
     */
    private FileUtils2(Context context) {
        this.mContext = context;
    }

    public static void init(Context context) {
        if (sFileUtils2 == null) {
            sFileUtils2 = new FileUtils2(context);
        }
    }

    public static FileUtils2 getInstance() {
        if (sFileUtils2 == null) {
            throw new NullPointerException("请初始化 FileUtils.init");
        }
        return sFileUtils2;
    }

    /**
     * 获取储存的目录
     *
     * @return
     */
    public static String getStorageDirectory(Context context) {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                Environment.getExternalStorageDirectory().getAbsolutePath() : context.getCacheDir().getPath();
    }

    /**
     * 创建目录
     *
     * @param dirsName 目录
     * @return
     */
    public File createDir(String dirsName) throws IOException {
        File file = new File(getStorageDirectory(mContext) + dirsName);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return file;
            } else {
                throw new IOException("创建文件夹失败");
            }
        }
        scannerFile(file.getPath());
        return file;
    }

    /**
     * 在ＳＤ卡上 项目根目录 创建文件
     *
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public File createSDFile(String dirsName, String fileName) throws IOException {
        File dir = createDir(dirsName);//创建文件夹
        if (dir.exists()) {
            File file = new File(dir.getPath() + File.separator + fileName);
            if (!file.exists()) {
                if (file.createNewFile()) {//创建文件
                    scannerFile(file.getPath());
                    return file;
                } else {
                    throw new IOException("创建文件失败");
                }
            } else {
                return file;
            }
        }
        return null;
    }

    /**
     * 判断SD 卡上的文件夹内的 文件是否存在
     *
     * @param dirsName
     * @param fileName
     * @return
     */
    public boolean isFileExist(String dirsName, String fileName) {
        File file = new File(getStorageDirectory(mContext) + dirsName + File.separator + fileName);
        return file.exists();
    }

    /**
     * 获取文件
     *
     * @param dirsName
     * @param fileName
     * @return
     */
    public File getFile(String dirsName, String fileName) {
        if (isFileExist(dirsName, fileName)) {
            return new File(getStorageDirectory(mContext) + dirsName + File.separator + fileName);
        }
        return null;
    }

    /**
     * 将 InputStream保存到 SD卡中
     * 写入监听 mOutputStreamListener
     *
     * @param path     文件路径
     * @param fileName 文件名
     * @param input    输入流
     */
    public File writeSDFormInput(String path, String fileName, InputStream input, OnOutputStreamListener mOutputStreamListener) {
        File file = null;
        OutputStream outputStream = null;
        try {
            file = createSDFile(path, fileName);
            outputStream = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int length;
            while ((length = input.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
                if (mOutputStreamListener != null) {
                    mOutputStreamListener.onReadLength(length);
                }
            }
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace(); // To change body of catch statement use File |
            // Settings | File Templates.
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close(); // 清除缓存
            } catch (IOException e) {
                e.printStackTrace(); // To change body of catch statement use
                // File | Settings | File Templates.
            }
        }
        return file;
    }

    /**
     * 更新sd文件列表信息
     * 告诉傻缺Android， 我放文件了，请及时显示 扫描文件 防止文件放入磁盘中，未能及时查看
     *
     * @param filePath
     */
    public void scannerFile(String... filePath) {
        MediaScannerConnection.scanFile(mContext, filePath, null, null);
    }

    /**
     * 写入本地速度监听
     * <p>
     * author abook23
     */
    public interface OnOutputStreamListener {
        /**
         * @param size 写入量
         */
        void onReadLength(int size);
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public void deleteAllFile(File file) {
        AndroidUtils.deleteDirectory(file);
    }
}

package com.android.easy.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Environment;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 相机管理
 *
 * author My.Y
 */
public abstract class CameraManager {
    public static final int PREVIEW_SIZE_1080P = 1080;
    public static final int PREVIEW_SIZE_960P = 960;
    public static final int PREVIEW_SIZE_720P = 720;
    public static final int PREVIEW_SIZE_480P = 480;

    public static int mVideoMaxDuration = 10000;//默认录制时间长度
    public static long mVideoMaxZie = 50 * 1024 * 1024;//默认录制视频大小
    public static float mVideoRatio = 2f;


    protected CameraManagerListener mCameraManagerListener;
    protected OnCameraListener mCameraListener;

    protected String mVideoPath;
    protected Context context;
    protected int cameraId;

    protected static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public CameraManager(Context context, CameraManagerListener onCameraManagerListener) {
        this.context = context;
        mCameraManagerListener = onCameraManagerListener;
    }

    public static boolean DEBUG = false;

    /**
     * 打开相机
     */
    public abstract void openCamera(int cameraId, int height);

    /**
     * 预览
     *
     * @param surface
     */
    abstract void startPreview(SurfaceTexture surface, int width, int height);

    /**
     * 关闭相机
     */
    abstract void closeCamera();

    public abstract void zoomCamera(int value);

    public abstract void doAutoFocus();

    public abstract void doAutoFocus(int x, int y);

    public abstract void openFlash();

    /**
     * 照相
     */
    public abstract void toCapture();

    public abstract void startRecordVideo();

    public abstract void stopRecordVideo();

    public void setCameraListener(OnCameraListener cameraListener) {
        mCameraListener = cameraListener;
    }

    public void setVideoRatio(float videoRatio) {
        if (videoRatio > 5.0F) {//太大，文件很大
            videoRatio = 5.0F;
        }
        mVideoRatio = videoRatio;
    }

    protected int getOrientation() {
        int rotation = context.getResources().getConfiguration().orientation;
        if (cameraId == CameraCharacteristics.LENS_FACING_FRONT) {
            return ORIENTATIONS.get(rotation);
        } else {
            return 360 - ORIENTATIONS.get(rotation);
        }
    }

    protected int getOrientationDegrees() {
        if (cameraId == CameraCharacteristics.LENS_FACING_FRONT) {
            return (getOrientation() + 90) % 360;
        } else {
            return (getOrientation() + 270) % 360;
        }
    }

    protected static Size chooseOptimalSize(Size[] supportedSizes, int previewSize, float rate) {
        int k = 0;
        int calcWidth = 0;
        int calcHeight = 0;
        for (int i = 0; i < supportedSizes.length; i++) {
            Size size = supportedSizes[i];
            int width = size.getWidth();
            int height = size.getHeight();
            if (equalRate(size, rate)) {
                if (width >= calcWidth && height >= calcHeight && height <= previewSize) {
                    calcWidth = width;
                    calcHeight = height;
                    k = i;
                }
            }
        }
        return supportedSizes[k];
    }


    /**
     * 比例
     *
     * @param size
     * @param rate 16:9 1.777777
     * @return
     */
    private static boolean equalRate(Size size, float rate) {
        if (rate <= 0) {
            return true;
        }
        return (double) Math.abs((float) size.getWidth() / (float) size.getHeight() - rate) <= 0.2D;
    }

    public void configureTransform(CameraView cameraView, int viewWidth, int viewHeight) {
        int rotation = context.getResources().getConfiguration().orientation;
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, cameraView.getHeight(), cameraView.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / cameraView.getHeight(),
                    (float) viewWidth / cameraView.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        cameraView.setTransform(matrix);
    }

    public String writeImageToFile(byte[] data) {
        String photoPath = getImagePath();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        matrix.preRotate((float) getOrientationDegrees());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        try {
            FileOutputStream os = new FileOutputStream(new File(photoPath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            bitmap.recycle();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.gc();
        }
        return photoPath;
    }

    public String getImagePath() {
        String diskDir = getDiskDir(context, "/DCIM/Camera");
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(diskDir, fileName);
        return file.getAbsolutePath();
    }

    public String getVideoPath() {
        String diskDir = getDiskDir(context, "/DCIM/Camera");
        String fileName = System.currentTimeMillis() + ".mp4";
        File file = new File(diskDir, fileName);
        return file.getAbsolutePath();
    }

    public String getDiskDir(Context context, String dir) {
        String path;
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && !Environment.isExternalStorageRemovable()) {
            path = context.getCacheDir().getPath();
        } else {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        File file = new File(path, dir);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getPath();
    }


    public interface CameraManagerListener {
        void onCameraPreview(int previewWidth, int previewHeight);
    }
}

package com.android.easy.camera;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * author My
 * date 2017/12/13
 * 相机管理
 */
public class Camera1Manager extends CameraManager implements Camera.PreviewCallback {

    private String TAG = Camera1Manager.class.getSimpleName();

    private int numCameras = Camera.getNumberOfCameras(); // 初始化摄像头数量
    private Camera mCamera;
    private Camera.Parameters mParameters;
    private List<Camera.Size> mPictureSizes;
    private List<Camera.Size> mPreviewSizes;
    private Camera.Parameters parameters;
    private byte[] mBuffer;
    private boolean previewing;

    private SurfaceTexture mSurfaceTexture;
    private MediaRecorder mMediaRecorder;
    private int surfaceWidth,surfaceHeight;

    public Camera1Manager(Context context, CameraManagerListener cameraManagerListener) {
        super(context,cameraManagerListener);
    }

    public boolean isPreviewing() {
        return previewing;
    }

    @Override
    public void openCamera(int cameraId, int height) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        //有效的 cameraId 时,打开当前摄像头,否则打开后置摄像头
        cameraId = cameraId >= 0 && cameraId < numCameras ? cameraId : Camera.CameraInfo.CAMERA_FACING_BACK;
        mCamera = Camera.open(cameraId);

        mCamera.setDisplayOrientation(90);//设置旋转90度

        mParameters = mCamera.getParameters();
        mPictureSizes = mParameters.getSupportedPictureSizes();
        mPreviewSizes = mParameters.getSupportedPreviewSizes();
        Collections.reverse(mPictureSizes);
        Collections.reverse(mPreviewSizes);

        //选择合适的分辨率
        Camera.Size size = calculateCameraFrameSize(mPreviewSizes, height, 0);
        Log.i(TAG, "Camera.Size:" + size.width + "x" + size.height);
        //设置预览 与 照片
        setParameters(height, size.width, size.height);
        addCameraCallBack();
    }

    @Override
    public void startPreview(SurfaceTexture surface, int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
        mSurfaceTexture = surface;
        if (previewing) {
            stopPreview();
        }
        try {
            mCamera.setPreviewCallback(this);
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            previewing = true;
            Log.d(TAG, "startPreview");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPreview() {
        if (mCamera != null && previewing) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);  // Camera is being used after Camera.release() was called
            previewing = false;
            Log.d(TAG, "stopPreview");
        }
    }

    @Override
    public void closeCamera() {
        if (mCamera != null) {
            stopPreview();
            mCamera.release();
        }
        mCamera = null;
    }

    @Override
    public void zoomCamera(int value) {
        Camera.Parameters parameters = mCamera.getParameters();
        int zoom = parameters.getZoom();
        zoom += value;
        if (zoom < 0) {
            zoom = 0;
        }
        if (zoom > parameters.getMaxZoom()) {
            zoom = parameters.getMaxZoom();
        }
        parameters.setZoom(zoom);
        mCamera.setParameters(parameters);
    }

    private boolean isFlash;

    @Override
    public void openFlash() {
        Camera.Parameters parameters = mCamera.getParameters();
        if (!isFlash) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        } else {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        mCamera.setParameters(parameters);
        isFlash = !isFlash;
    }

    @Override
    public void toCapture() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                mCameraListener.onPictureTaken(data);
            }
        });
    }

    @Override
    public void startRecordVideo() {
        try {
            mMediaRecorder = initVideo();
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopRecordVideo() {
        mMediaRecorder.stop();
//        mMediaRecorder.reset();
        mMediaRecorder.release();
    }

    /**
     * 初始化录像机
     */
    private MediaRecorder initVideo() {
        mVideoPath = getVideoPath();
        MediaRecorder recorder = new MediaRecorder();
        final int orientation = context.getResources().getConfiguration().orientation;
        if (mCamera == null) {
            mCamera = Camera.open(0);
            mCamera.setDisplayOrientation(orientation);
        }

        mCamera.unlock();
        recorder.setCamera(mCamera);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setVideoFrameRate(25);

        Camera.Parameters params = mCamera.getParameters();
        int previewWidth = params.getPreviewSize().width;
        int previewHeight = params.getPreviewSize().height;
        recorder.setVideoSize(previewWidth, previewHeight);

        recorder.setMaxDuration(mVideoMaxDuration);
        recorder.setMaxFileSize(mVideoMaxZie);
        recorder.setVideoEncodingBitRate((int) (mVideoRatio * 1024.0F * 1024.0F));
        recorder.setAudioChannels(2);
        recorder.setAudioEncodingBitRate(128);
        recorder.setOrientationHint(orientation);

        recorder.setOutputFile(mVideoPath);
//        recorder.setPreviewDisplay();
        return recorder;
    }


    private void setParameters(int pictureSize, int width, int height) {
        float rate = (float) width / height;//比例
        rate = Float.valueOf(String.format(Locale.getDefault(), "%.2f", rate));
        Log.i(TAG, "相机预览:" + width + "x" + height + "--" + pictureSize + " dpi----rate:" + rate + "---");
        Camera.Size size;
        if (pictureSize > 0) {
            size = calculateCameraFrameSize(mPictureSizes, pictureSize, rate);
        } else {
            size = mPictureSizes.get(mPictureSizes.size() - 1);
        }
        mParameters.setPictureSize(size.width, size.height);
        Log.i(TAG, "设置图片:w = " + size.width + "x" + size.height + "--rate:" + (float) size.width / size.height);

        size = calculateCameraFrameSize(mPreviewSizes, pictureSize, rate);
        mParameters.setPreviewSize(size.width, size.height);
        Log.i(TAG, "设置预览:w = " + size.width + "x" + size.height + "--rate:" + (float) size.width / size.height);

        mParameters.setPreviewFormat(ImageFormat.NV21);

        List<String> FocusModes = mParameters.getSupportedFocusModes();
        if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        //mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//连续对焦

        //以下两句 影响 小米5 自动对焦
//        mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_STEADYPHOTO);
//        mParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);

        try {
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCameraCallBack() {
        Camera.Parameters params = mCamera.getParameters();
        int mFrameWidth = params.getPreviewSize().width;
        int mFrameHeight = params.getPreviewSize().height;

        int pxSize = mFrameWidth * mFrameHeight;
        pxSize = pxSize * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
        mBuffer = new byte[pxSize];

        mCamera.addCallbackBuffer(mBuffer);
        mCamera.setPreviewCallbackWithBuffer(this);

        mCameraManagerListener.onCameraPreview(mFrameWidth, mFrameHeight);
    }

    protected Camera.Size calculateCameraFrameSize(List<Camera.Size> supportedSizes, int previewSize, float rate) {
        int k = 0;
        int calcWidth = 0;
        int calcHeight = 0;
        for (int i = 0; i < supportedSizes.size(); i++) {
            Camera.Size size = supportedSizes.get(i);
            int width = size.width;
            int height = size.height;
            if (equalRate(size, rate)) {
                if (DEBUG) {
                    Log.i(TAG, "CameraSize:" + width + "x" + height + "--rate:" + (float) width / height);
                }
                //1088 这个像素容易出错
                if (width >= calcWidth && height >= calcHeight && height <= previewSize && height != 1088) {//960
                    calcWidth = width;
                    calcHeight = height;
                    k = i;
                }
            }
        }
        return supportedSizes.get(k);
    }

    /**
     * 比例
     *
     * @param size
     * @param rate 16:9 1.777777
     * @return
     */
    private boolean equalRate(Camera.Size size, float rate) {
        if (rate <= 0) {
            return true;
        }
        return (double) Math.abs((float) size.width / (float) size.height - rate) <= 0.2D;
    }

    /**
     * 聚焦
     */
    @Override
    public void doAutoFocus() {
        try {
            mCamera.cancelAutoFocus();
            parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
                        if (!Build.MODEL.equals("KORIDY H30")) {
                            parameters = camera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);// 1连续对焦
//                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                            camera.setParameters(parameters);
                        } else {
                            parameters = camera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                            camera.setParameters(parameters);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doAutoFocus(int x, int y) {
        try {
            mCamera.cancelAutoFocus();
            parameters = mCamera.getParameters();

            //聚焦点击位置
            if (x > 0 && y > 0) {
                List<Camera.Area> areas = new ArrayList<>();
                List<Camera.Area> areasMetrix = new ArrayList<>();
                Camera.Size previewSize = parameters.getPreviewSize();
                Rect focusRect = calculateTapArea(x, y, 1.0f, previewSize);
                Rect metrixRect = calculateTapArea(x, y, 1.5f, previewSize);
                areas.add(new Camera.Area(focusRect, 1000));
                areasMetrix.add(new Camera.Area(metrixRect, 1000));
                parameters.setMeteringAreas(areasMetrix);
                parameters.setFocusAreas(areas);
            }

            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.cancelAutoFocus();// 只有加上了这一句，才会自动对焦。
                        if (!Build.MODEL.equals("KORIDY H30")) {
                            parameters = camera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
                            camera.setParameters(parameters);
                        } else {
                            parameters = camera.getParameters();
                            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                            camera.setParameters(parameters);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Rect calculateTapArea(float x, float y, float coefficient, Camera.Size previewSize) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerY = (int) (x / surfaceWidth * 2000 - 1000);
        int centerX = (int) (y / surfaceHeight * 2000 - 1000);
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);

        RectF rectF = new RectF(left, top, left + areaSize, top + areaSize);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (mCameraListener != null) {
            mCameraListener.onPreviewFrame(bytes);
        }
        if (mCamera != null) {
            mCamera.addCallbackBuffer(mBuffer);
        }
    }
}

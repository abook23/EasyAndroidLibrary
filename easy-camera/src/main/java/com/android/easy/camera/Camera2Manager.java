package com.android.easy.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

/**
 * author My
 * date 2019-9-23
 * 相机管理
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Manager extends CameraManager {
    private String TAG = Camera1Manager.class.getSimpleName();
    private android.hardware.camera2.CameraManager mCameraManager;
    private Integer sensorOrientation;
    private Size previewSize;

    private SurfaceTexture texture;
    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private final Semaphore cameraOpenCloseLock = new Semaphore(1);
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder previewRequestBuilder;
    private ImageReader previewReader;
    private ImageReader mImageReader;
    private CameraCaptureSession captureSession;
    private CaptureRequest previewRequest;

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraOpenCloseLock.release();
            mCameraDevice = camera;
            mCameraManagerListener.onCameraPreview(previewSize.getWidth(), previewSize.getHeight());
            //相机装备好了，可以开始预览
            if (texture != null) {
                createCameraPreviewSession();
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraOpenCloseLock.release();
            camera.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraOpenCloseLock.release();
            camera.close();
            mCameraDevice = null;
        }
    };

    private final CameraCaptureSession.CaptureCallback captureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureProgressed(
                final CameraCaptureSession session,
                final CaptureRequest request,
                final CaptureResult partialResult) {
        }

        @Override
        public void onCaptureCompleted(
                final CameraCaptureSession session,
                final CaptureRequest request,
                final TotalCaptureResult result) {
        }
    };

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private MediaRecorder mMediaRecorder;
    private Surface textureSurface;

    public Camera2Manager(Context context, CameraManagerListener cameraManagerListener) {
        super(context, cameraManagerListener);
        mCameraManager = (android.hardware.camera2.CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("ImageListener");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        backgroundThread.quitSafely();
        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (final InterruptedException e) {
            Log.e(TAG,"Exception!");
        }
    }

    @Override
    @RequiresPermission(android.Manifest.permission.CAMERA)
    public void openCamera(int cameraId, int height) {
        this.cameraId = cameraId;
        if (mCameraDevice != null) {
            closeCamera();
        }
        startBackgroundThread();
        try {
            //获取相机
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(String.valueOf(cameraId));
            //
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);

            previewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), height, 0);

//            final int orientation = getResources().getConfiguration().orientation;
//            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//            }
            mCameraManager.openCamera(String.valueOf(cameraId), mStateCallback, backgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    void startPreview(SurfaceTexture surface, int width, int height) {
        texture = surface;
        startBackgroundThread();
        createCameraPreviewSession();
    }

    @Override
    void closeCamera() {
        try {
            cameraOpenCloseLock.acquire();
            if (null != captureSession) {
                captureSession.close();
                captureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != previewReader) {
                previewReader.close();
                previewReader = null;
            }
        } catch (final InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            cameraOpenCloseLock.release();
        }
        stopBackgroundThread();
    }

    @Override
    public void zoomCamera(int value) {

    }

    @Override
    public void doAutoFocus() {

    }

    @Override
    public void doAutoFocus(int x, int y) {

    }

    private boolean flashStatus;

    @Override
    public void openFlash() {
        try {
            flashStatus = !flashStatus;
            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, flashStatus ? CameraMetadata.FLASH_MODE_TORCH : CameraMetadata.FLASH_MODE_OFF);
            previewRequest = previewRequestBuilder.build();
            captureSession.setRepeatingRequest(previewRequest, captureCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            assert texture != null;
            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            // This is the output Surface we need to start preview.
            textureSurface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(textureSurface);

            Log.d(TAG,"Opening camera preview: " + previewSize.getWidth() + "x" + previewSize.getHeight());

            // Create the reader for the preview frames.
            previewReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.YUV_420_888, 2);
            previewReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = reader.acquireNextImage();
                    if (mCameraListener != null) {
                        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        mCameraListener.onPreviewFrame(bytes);
                    }
                    image.close();
                }
            }, backgroundHandler);
            previewRequestBuilder.addTarget(previewReader.getSurface());

            //前三个参数分别是需要的尺寸和格式，最后一个参数代表每次最多获取几帧数据，本例的2代表ImageReader中最多可以获取两帧图像流
            mImageReader = ImageReader.newInstance(previewSize.getWidth(), previewSize.getHeight(), ImageFormat.JPEG, 2);
            //监听ImageReader的事件，当有图像流数据可用时会回调onImageAvailable方法，它的参数就是预览帧数据，可以对这帧数据进行处理
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    if (mCameraListener != null) {
                        Image image = reader.acquireLatestImage();
                        //我们可以将这帧数据转成字节数组，类似于Camera1的PreviewCallback回调的预览帧数据
                        if (image == null) {
                            return;
                        }
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] data = new byte[buffer.remaining()];
                        buffer.get(data);
                        image.close();
                        mCameraListener.onPictureTaken(data);
                    }
                }
            }, null);

            // Here, we create a CameraCaptureSession for camera preview.
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，
            // 当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice.createCaptureSession(Arrays.asList(textureSurface, previewReader.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    updatePreview(mCameraDevice, cameraCaptureSession);
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, null);
        } catch (final CameraAccessException e) {
            Log.e(TAG,"Exception!");
        }
    }

    @Override
    public void toCapture() {
        if (mCameraDevice == null) {
            return;
        }
        try {
            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());//拍照时，是将mImageReader.getSurface()作为目标
            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation());

            captureSession.capture(captureBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    //拍照完成
                    try {
                        captureSession.setRepeatingRequest(previewRequest, captureCallback, backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void startRecordVideo() {
        try {
            setUpMediaRecorder();
            assert texture != null;
            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            // This is the output Surface we need to start preview.
            final Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            previewRequestBuilder.addTarget(surface);

            Surface recorderSurface = mMediaRecorder.getSurface();
            previewRequestBuilder.addTarget(recorderSurface);

            Log.d(TAG,"Opening camera preview: " + previewSize.getWidth() + "x" + previewSize.getHeight());

            // Here, we create a CameraCaptureSession for camera preview.
            //创建相机捕获会话，第一个参数是捕获数据的输出Surface列表，第二个参数是CameraCaptureSession的状态回调接口，
            // 当它创建好后会回调onConfigured方法，第三个参数用来确定Callback在哪个线程执行，为null的话就在当前线程执行
            mCameraDevice.createCaptureSession(Arrays.asList(surface, recorderSurface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    updatePreview(mCameraDevice, cameraCaptureSession);
                    mMediaRecorder.start();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopRecordVideo() {
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        if (mCameraListener != null) {
            mCameraListener.onRecordVideo(mVideoPath);
        }
    }

    private void setUpMediaRecorder() throws IOException {
        mVideoPath = getVideoPath();
        int degrees = getOrientationDegrees();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //VOICE_RECOGNITION相比于MIC会根据语音识别的需要做一些调谐，当然，这需要在系统支持的情况下。
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mVideoPath);
        mMediaRecorder.setVideoFrameRate(30);
        mMediaRecorder.setVideoSize(previewSize.getWidth(), previewSize.getHeight());
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        mMediaRecorder.setOrientationHint(degrees);

        mMediaRecorder.setMaxDuration(mVideoMaxDuration);
        mMediaRecorder.setMaxFileSize(mVideoMaxZie);
        mMediaRecorder.setVideoEncodingBitRate((int) (mVideoRatio * 1024.0F * 1024.0F));
        mMediaRecorder.setAudioChannels(2);
        mMediaRecorder.setAudioEncodingBitRate(128);

        mMediaRecorder.prepare();
    }


    private void updatePreview(CameraDevice mCameraDevice, CameraCaptureSession cameraCaptureSession) {
        if (null == mCameraDevice) {
            return;
        }
        // When the session is ready, we start displaying the preview.
        captureSession = cameraCaptureSession;
        try {
            // Auto focus should be continuous for camera preview.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // Flash is automatically enabled when necessary.
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // Finally, we start displaying the camera preview.
            previewRequest = previewRequestBuilder.build();
            //设置反复捕获数据的请求，这样预览界面就会一直有数据显示
            captureSession.setRepeatingRequest(previewRequest, captureCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG,e.getMessage());
        }
    }

}

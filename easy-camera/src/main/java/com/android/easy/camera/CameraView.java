package com.android.easy.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

/**
 * Created by My on 2017/12/13.
 */

public class CameraView extends TextureView implements CameraManager.CameraManagerListener, TextureView.SurfaceTextureListener {

    private CameraManager mCameraManager;
    private int mPreviewWidth, mPreviewHeight;
    private SurfaceTexture mSurfaceTexture;

    public void setListener(OnCameraListener listener) {
        getCameraManager().mCameraListener = listener;
    }

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mPreviewHeight > 0) {
            int width = resolveSize(0, widthMeasureSpec);
            int height = resolveSize(width * mPreviewWidth / mPreviewHeight, heightMeasureSpec);
            setMeasuredDimension(width, height);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        setSurfaceTextureListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraManager = new Camera2Manager(getContext(),this);
        } else {
            mCameraManager = new Camera1Manager(getContext(),this);
        }
    }

    public CameraManager getCameraManager() {
        return mCameraManager;
    }

    public void openCamera(int cameraId, int height) {
        mCameraManager.openCamera(cameraId,height);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurfaceTexture = surface;
        mCameraManager.startPreview(mSurfaceTexture,width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCameraManager.closeCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Override
    public void onCameraPreview(int previewWidth, int previewHeight) {
        Log.d("onCameraPreview","onCamera");
        mPreviewWidth = previewWidth;
        mPreviewHeight = previewHeight;
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

}

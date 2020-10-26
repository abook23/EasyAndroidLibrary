
package com.android.easy.mediastore;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.easy.camera.CameraManager;
import com.android.easy.camera.CameraView;
import com.android.easy.camera.OnCameraListener;
import com.android.easy.madiastore.R;
import com.android.easy.mediastore.utils.AutoFocusManager;
import com.android.easy.mediastore.utils.PermissionUtil;
import com.android.easy.mediastore.widget.VideoProgress;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.sprylab.android.widget.TextureVideoView;

import java.io.File;
import java.io.FileOutputStream;

public class CameraVideoFragment extends Fragment implements OnClickListener {
    private static String[] PERMISSIONS_CAMERA = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_CONTACTS = 1;
    private static String TAG = CameraVideoFragment.class.getSimpleName();
    private String PHOTO_PATH;
    ImageView cameraTransform;
    private Context context;
    ImageView iVvHd;
    private boolean isPlayVideo;
    ImageView mCameraBack;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    ImageView mCameraYes;
    private CountDownTimer mCountDownTimer;
    private Definition mDefinition;
    private DirectionOrientationListener mDirectionOrientationListener;
    private OnCameraVideoListener mListener;
    private OnCameraVideoTouchListener mTouchListener;
    TextView mTvSecond;
    private int mVideoMaxDuration = 10000;//默认录制时间长度
    private long mVideoMaxZie = 50 * 1024 * 1024;//默认录制视频大小
    VideoProgress mVideoProgress;
    TextureVideoView mVideoView;
    CameraView mCameraView;
    private CameraManager mCameraManager;
    private int mOrientation;
    private float mVideoRatio;
    private String VIDEO_PATH;
    private PhotoView photoView;

    public static CameraVideoFragment newInstance() {
        CameraVideoFragment cameraVideoFragment = new CameraVideoFragment();
        cameraVideoFragment.setArguments(new Bundle());
        return cameraVideoFragment;
    }

    public void setOnCameraVideoListener(OnCameraVideoListener listener) {
        mListener = listener;
    }

    public void setOnCameraVideoTouchListener(OnCameraVideoTouchListener listener) {
        mTouchListener = listener;
    }

    public CameraVideoFragment() {
        mDefinition = Definition.SD;
        mCameraId = 0;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_camera_video, viewGroup, false);
        initView(view);
        initDisplayMetrics();
        context = getContext();
        mVideoProgress.setOnCameraVideoListener(new ClickListener());
        if (mDefinition == Definition.HD) {
            iVvHd.setBackgroundResource(R.mipmap.gb_gallery_hd);
        } else {
            iVvHd.setBackgroundResource(R.mipmap.gb_gallery_hd_off);
        }
        mCameraManager = mCameraView.getCameraManager();
        initCountDownTimer();
        addListener();
        requestContactsPermissions();
        toFullscreen();
        mDirectionOrientationListener = new DirectionOrientationListener(context, 3);
        if (mDirectionOrientationListener.canDetectOrientation()) {
            mDirectionOrientationListener.enable();
            return view;
        } else {
            Log.d("chengcj1", "Can't Detect Orientation");
            return view;
        }
    }

    private void initView(View view) {
        mCameraView = view.findViewById(R.id.cameraView);
        mVideoProgress = view.findViewById(R.id.videoProgress);
        mTvSecond = view.findViewById(R.id.tv_second);
        iVvHd = view.findViewById(R.id.iv_hd);
        mCameraYes = view.findViewById(R.id.camera_yes);
        mCameraBack = view.findViewById(R.id.camera_back);
        mVideoView = view.findViewById(R.id.videoView);
        cameraTransform = view.findViewById(R.id.iv_camera_transform);
        photoView = view.findViewById(R.id.photoView);
    }

    private void addListener() {
        mTvSecond.setOnClickListener(this);
        iVvHd.setOnClickListener(this);
        mCameraYes.setOnClickListener(this);
        mCameraBack.setOnClickListener(this);
        cameraTransform.setOnClickListener(this);
        mCameraManager.setCameraListener(new OnCameraListener() {
            @Override
            public void onPreviewFrame(byte[] bytes) {

            }

            @Override
            public void onPictureTaken(byte[] bytes) {
                PHOTO_PATH = mCameraManager.writeImageToFile(bytes);
                MediaScannerConnection.scanFile(context, new String[]{PHOTO_PATH}, new String[]{MediaStoreConfig.MIME_TYPE_IMAGE}, null);
                photoView.post(new Runnable() {
                    @Override
                    public void run() {
                        photoView.setVisibility(View.VISIBLE);
                        Glide.with(context).load(PHOTO_PATH).into(photoView);
                        startAnimator1();
                    }
                });
            }

            @Override
            public void onRecordVideo(String path) {
                VIDEO_PATH = path;
                MediaScannerConnection.scanFile(context, new String[]{VIDEO_PATH}, new String[]{MediaStoreConfig.MIME_TYPE_VIDEO}, null);
                playVideo(path);
            }
        });
    }

    private void initDisplayMetrics() {
//        Resources resources = getResources();
//        DisplayMetrics dm = resources.getDisplayMetrics();
//        float density = dm.density;
//        widthDisplay = dm.widthPixels;
//        heightDisplay = dm.heightPixels;
    }

    private void requestContactsPermissions() {
        if (PermissionUtil.requestPermission(this, PERMISSIONS_CAMERA, REQUEST_CONTACTS)) {
            openCamera();
        }
    }

    private void openCamera() {
        mCameraView.setVisibility(View.VISIBLE);
        mCameraManager.openCamera(mCameraId, CameraManager.PREVIEW_SIZE_960P);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == 1) {
            if (!PermissionUtil.verifyPermissions(this, PERMISSIONS_CAMERA, results)) {
                Log.i(TAG, "缺少必要的权限");
                Toast.makeText(context, "缺少 相应权限", Toast.LENGTH_SHORT).show();
                getActivity().finish();
                return;
            }
            Log.i(TAG, "已经全部授权");
        }

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.iv_hd) {//高清
            if (mDefinition == Definition.SD) {
                mDefinition = Definition.HD;
                iVvHd.setBackgroundResource(R.mipmap.gb_gallery_hd);
            } else {
                mDefinition = Definition.SD;
                iVvHd.setBackgroundResource(R.mipmap.gb_gallery_hd_off);
            }
            openCamera();
        } else if (viewId == R.id.iv_camera_transform) {//前后摄像头切换
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
            openCamera();
        } else if (viewId == R.id.camera_yes) {//选择当前拍摄
            if (mTouchListener != null) {
                mTouchListener.onSuccess(PHOTO_PATH);
            }
            if (mListener != null) {
                if (isPlayVideo) {
                    mListener.onFragmentResult(VIDEO_PATH, "mp4");
                    return;
                }
                mListener.onFragmentResult(PHOTO_PATH, "jpg");
            }
        } else if (viewId == R.id.camera_back) {//放弃拍摄
            startAnimator2();
            photoView.setVisibility(View.GONE);
            mTvSecond.setText(getString(R.string.video_init_msg));
            if (isPlayVideo) {
                stopVideo();
                delFile(VIDEO_PATH);
            } else {
                delFile(PHOTO_PATH);
            }
            if (mTouchListener != null) {
                mTouchListener.onCancel();
            }
        }
    }

    private void delFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
        }
    }

    /**
     * 定时器
     */
    private void initCountDownTimer() {
        mCountDownTimer = new CountDownTimer(mVideoMaxDuration, 10) {
            @Override
            public void onFinish() {
                mVideoProgress.setProgress(mVideoMaxDuration);
                mTvSecond.setText((mVideoMaxDuration / 1000) + "s");
            }

            @Override
            public void onTick(long timer) {
                mVideoProgress.setMax(mVideoMaxDuration);
                float var3 = mVideoMaxDuration - timer;
                mVideoProgress.setProgress(var3);
                mTvSecond.setText((int) var3 / 1000 + "s");
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDirectionOrientationListener.disable();
    }

    public void setDefinition(Definition var1) {
        mDefinition = var1;
    }


    public void setVideoMaxDuration(int maxDuration) {
        mVideoMaxDuration = maxDuration;
    }

    public void setVideoMaxZie(long maxZie) {
        mVideoMaxZie = maxZie;
    }

    public void setVideoRatio(float videoRatio) {
        if (videoRatio > 5.0F) {
            videoRatio = 5.0F;
        }
        mVideoRatio = videoRatio;
    }

    private class ClickListener implements VideoProgress.OnClickListener {
        @Override
        public void onClick() {
            mCameraManager.toCapture();
            if (mTouchListener != null) {
                mTouchListener.onClick();
            }
        }

        @Override
        public void onLongClick() {
            mCountDownTimer.start();
            mCameraManager.startRecordVideo();
            if (mTouchListener != null) {
                mTouchListener.onLongClick();
            }
        }

        @Override
        public void onLongUpClick() {
            mCountDownTimer.cancel();
            mCameraManager.stopRecordVideo();
            startAnimator1();
        }
    }

    /**
     * 播放视频
     */
    private void playVideo(String path) {
        isPlayVideo = true;
        mVideoView.setVisibility(View.VISIBLE);
        mCameraView.setVisibility(View.GONE);
        cameraTransform.setVisibility(View.GONE);
        iVvHd.setVisibility(View.GONE);
        mVideoView.setVideoURI(Uri.parse(path));
        mVideoView.setOnErrorListener(new OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
        mVideoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
        mVideoView.start();
        mVideoView.setOnInfoListener(new OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    /**
     * 停止视频
     */
    private void stopVideo() {
        isPlayVideo = false;
        iVvHd.setVisibility(View.VISIBLE);
        cameraTransform.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.GONE);
        mVideoView.pause();
        mVideoView.stopPlayback();

        openCamera();
    }

    /**
     * 保存照片
     */
    private void savePicture(final byte[] data) {

    }

    //动画
    private void startAnimator1() {
        mTvSecond.setVisibility(View.GONE);
        mVideoProgress.setVisibility(View.GONE);
        mCameraYes.setVisibility(View.VISIBLE);
        mCameraBack.setVisibility(View.VISIBLE);
        float var1 = mCameraYes.getTranslationX();
        float var2 = mCameraYes.getTranslationX();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mCameraYes, "translationX", var1, (float) dp2px(context, 80.0F));
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mCameraBack, "translationX", var2, (float) (-dp2px(context, 80.0F)));
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mVideoProgress, "alpha", 1.0F, 0.0F);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mCameraYes, "alpha", 0.0F, 1.0F);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mCameraBack, "alpha", 0.0F, 1.0F);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1, animator2, animator3, animator4, animator5);
        animatorSet.setDuration(300L);
        animatorSet.start();
    }

    private void startAnimator2() {
        mVideoProgress.setVisibility(View.VISIBLE);
        mCameraYes.setVisibility(View.GONE);
        mCameraBack.setVisibility(View.GONE);
        mTvSecond.setVisibility(View.VISIBLE);
        float var1 = mCameraYes.getTranslationX();
        float var2 = mCameraYes.getTranslationX();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mCameraYes, "translationX", var1, 0.0F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mCameraBack, "translationX", -var2, 0.0F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mVideoProgress, "alpha", 0.0F, 1.0F);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mCameraYes, "alpha", 1.0F, 0.0F);
        ObjectAnimator animator5 = ObjectAnimator.ofFloat(mCameraBack, "alpha", 1.0F, 0.0F);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator1, animator2, animator3, animator4, animator5);
        animatorSet.setDuration(300L);
        animatorSet.start();
    }

    private void setViewVisibility(boolean b, View... views) {
        for (View view : views) {
            view.setVisibility(b ? View.VISIBLE : View.GONE);
        }
    }

    private int dp2px(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5F);
    }

    /**
     *
     */
    public enum Definition {
        HD, SD
    }

    public void toFullscreen() {
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
    }

    public void clearFullscreen() {
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);// 取消全屏
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 重力感应
     */
    public class DirectionOrientationListener extends OrientationEventListener {

        public DirectionOrientationListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation != -1) {
                orientation = (orientation + 45) / 90 * 90 % 360;
                if (orientation != mOrientation) {
                    mOrientation = orientation;
                    return;
                }
            }
        }
    }


    public interface OnCameraVideoListener {
        void onFragmentResult(String path, String type);
    }

    public interface OnCameraVideoTouchListener {
        void onLongClick();

        void onClick();

        void onSuccess(String path);

        void onCancel();
    }
}

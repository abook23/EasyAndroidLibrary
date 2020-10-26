package com.android.easy.play;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener {

    private View rootView;
    private boolean mEnableMediaCodec;
    private IMediaPlayer mMediaPlayer;
    /**
     * 播放url
     */
    private String playUrl;
    /**
     * 播放所有播放url
     */
//    private List<String> urls = new ArrayList<>();
    private List<MovieInfo> movieData = new ArrayList<>();
    /**
     * 加载中
     */
    private ProgressBar mProgressBar;
    /**
     * 播放进度条
     */
    private SeekBar mSeekBar;
    /**
     * 当前播放时间
     */
    private TextView positionTime;
    /**
     * video 时长
     */
    private TextView durationTime;
    /**
     * 暂停按钮
     */
    private ImageView pauseView;
    private View screenOrientationView, seekBarBottomView;
    /**
     * 锁屏按钮
     */
    private ImageView lockView;

    private View seekBarLinearLayout, appBarLinearLayout, dialogFrameLayout;
    /**
     * 是否暂停，是否锁屏
     */
    private boolean isPause, isLockView;
    /**
     * 进度条更新定时器
     */
    private Timer mTimer;
    /**
     * 是否在拖动进度条设置进度
     */
    private boolean isSettingSeekBar;
    /**
     * 选集
     */
    private TodAdapter mTodAdapter;
    private SurfaceHolder mSurfaceHolder;
    /**
     * 当前播放position
     */
    public int playPosition;

    private int bufIndex = -1;
    private float bufCount = 0f;

    public final static String VIDEO_PLAY_SHARED_PREFERENCES = "video_play_shared_preferences";
    private String videoName;
    private SurfaceView surfaceView;
    private int videoParentViewWidth, videoParentViewHeight;

    private View.OnClickListener onScreenOrientationClickListener;
    private OnVideoFragmentListener mOnVideoFragmentListener;
    private int mOrientation;
    private OrientationEventListener mOrientationEventListener;


    public VideoFragment() {
    }

    public static VideoFragment newInstance(String videoName, String playUrl) {
        return VideoFragment.newInstance(videoName, playUrl, new ArrayList<>());
    }

    public static VideoFragment newInstance(String videoName, String playUrl, List<MovieInfo> data) {
        VideoFragment fragment = new VideoFragment();
        fragment.videoName = videoName;
        fragment.playUrl = playUrl;
        fragment.movieData = data;
        return fragment;
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(VIDEO_PLAY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void addScreenOrientationClickListener(View.OnClickListener onClickListener) {
        onScreenOrientationClickListener = onClickListener;
    }

    public void setOnVideoFragmentListener(OnVideoFragmentListener onVideoFragmentListener) {
        mOnVideoFragmentListener = onVideoFragmentListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_video, container, false);
            mProgressBar = rootView.findViewById(R.id.progressBar);
            mSeekBar = rootView.findViewById(R.id.seekBar);
            positionTime = rootView.findViewById(R.id.positionTime);
            durationTime = rootView.findViewById(R.id.durationTime);
            pauseView = rootView.findViewById(R.id.pauseView);
            lockView = rootView.findViewById(R.id.lockView);
            seekBarLinearLayout = rootView.findViewById(R.id.seekBarLinearLayout);
            appBarLinearLayout = rootView.findViewById(R.id.appBarLinearLayout);
            dialogFrameLayout = rootView.findViewById(R.id.dialogFrameLayout);
            TextView videoNameTextView = rootView.findViewById(R.id.videoName);
            videoNameTextView.setText(videoName);
            dialogFrameLayout.setVisibility(View.GONE);

            if (movieData != null && !movieData.isEmpty()) {
                RecyclerView todRecyclerView = rootView.findViewById(R.id.todRecyclerView);
                todRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                todRecyclerView.setAdapter(mTodAdapter = new TodAdapter());
                mTodAdapter.setData(movieData);
                /**
                 * 选集
                 */
                mTodAdapter.setOnItemClickListener((viewHolder, position) -> {
                    play(movieData.get(position).getUrl());
                    dialogFrameLayout.setVisibility(View.GONE);
                });
            } else {
                rootView.findViewById(R.id.selectTodTextView).setVisibility(View.GONE);
            }

            surfaceView = rootView.findViewById(R.id.surfaceView);
            surfaceView.getHolder().addCallback(this);
            rootView.setOnClickListener(this);
            pauseView.setOnClickListener(this);
            lockView.setOnClickListener(this);
            dialogFrameLayout.setOnClickListener(this);
            rootView.findViewById(R.id.selectTodTextView).setOnClickListener(this);
            rootView.findViewById(R.id.backImageView).setOnClickListener(this);
            /**
             * 播放进度
             */
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mMediaPlayer != null) {
                        long seek = (long) ((float) progress / seekBar.getMax() * mMediaPlayer.getDuration());
                        positionTime.setText(DateUtils.formatterTime(seek));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    isSettingSeekBar = true;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (isLockView) {
                        return;
                    }
                    isSettingSeekBar = false;
                    seekTo(seekBar.getProgress());
                    showSettingView(true);
                }
            });
            seekBarBottomView = rootView.findViewById(R.id.seekBarBottomView);
            screenOrientationView = rootView.findViewById(R.id.screenOrientationView);
            screenOrientationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onScreenOrientationClickListener != null) {
                        onScreenOrientationClickListener.onClick(v);
                    }
                }
            });
            addOrientationEventListener();
            setOnTouchListener(rootView);
        }
        return rootView;
    }

    private void setOnTouchListener(View rootView) {
        rootView.setOnTouchListener(new View.OnTouchListener() {
            private PointF downPoint;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downPoint = new PointF(event.getX(), event.getY());
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    float x = event.getX() - downPoint.x;
                    float y = event.getY() - downPoint.y;
                    if (Math.abs(y) < 10) {//只往左右
                        if (x >= 20) {//向右
                            mSeekBar.setProgress(mSeekBar.getProgress() + 500);
                            mMediaPlayer.seekTo(mSeekBar.getProgress());
                        } else if (x < -20) {//向左
                            mSeekBar.setProgress(mSeekBar.getProgress() - 500);
                            mMediaPlayer.seekTo(mSeekBar.getProgress());
                        }
                    }

                }
                return false;
            }
        });
    }

    private void addOrientationEventListener() {
        mOrientationEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation != -1) {
                    orientation = (orientation + 45) / 90 * 90 % 360;
                    if (orientation != mOrientation) {
                        mOrientation = orientation;
                        try {
                            if (mOrientation == 90) {
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                            } else if (mOrientation == 270) {
                                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        };
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        } else {
            mOrientationEventListener.disable();//注销
        }
    }

    public boolean isLockView() {
        return isLockView;
    }

    private Runnable hideSettingViewRunnable;

    private void showSettingView(boolean b) {
        if (isSettingSeekBar) {
            return;
        }
        if (!isLockView) {//是否锁屏
            //seekBarLinearLayout
            setVisibilityView(b, pauseView/*暂停*/, appBarLinearLayout/*标题等*/, positionTime/*播放时间*/, durationTime/*片长时间*/, screenOrientationView/*横屏按钮*/, seekBarBottomView);
            mOnVideoFragmentListener.onSettingVisibilityView(b);
        }
        setVisibilityView(b, mSeekBar/*播放进度*/, lockView/*锁屏按钮*/);
        if (b) {
            if (hideSettingViewRunnable != null) {
                rootView.removeCallbacks(hideSettingViewRunnable);
            }
            hideSettingViewRunnable = () -> showSettingView(false);
            rootView.postDelayed(hideSettingViewRunnable, 3000);
        }
    }

    private void setVisibilityView(boolean b, View... views) {
        for (View view : views) {
            view.setVisibility(b ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
        if (isPause) {
            mMediaPlayer.setDisplay(mSurfaceHolder);
            start();
        } else {
            View videoParentFrameLayout = rootView.findViewById(R.id.videoParentFrameLayout);
            videoParentViewWidth = videoParentFrameLayout.getWidth();//获取父view 的高度
            videoParentViewHeight = videoParentFrameLayout.getHeight();
            play(playUrl);
        }
    }

    private void changedSurfaceViewSize(int width, int height) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        int deviceWidth = videoParentViewWidth;
        int deviceHeight = videoParentViewHeight;
        //下面进行求屏幕比例,因为横竖屏会改变屏幕宽度值,所以为了保持更小的值除更大的值.
        if (getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
            float devicePercent = (float) width / (float) height; //竖屏状态下宽度小与高度,求比
            if (deviceHeight > deviceWidth) {
                layoutParams.width = deviceWidth;
                layoutParams.height = (int) (deviceWidth / devicePercent);
            } else {
                layoutParams.width = (int) (deviceHeight * devicePercent);
                layoutParams.height = deviceHeight;
            }

        } else { //横屏
            float devicePercent = (float) height / (float) width; //横屏状态下高度小与宽度,求比
            layoutParams.width = (int) (deviceHeight / devicePercent);
            layoutParams.height = deviceHeight;
        }
        surfaceView.setLayoutParams(layoutParams);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
        if (mOnVideoFragmentListener != null && mMediaPlayer != null && mMediaPlayer.getDuration() > 0) {
            mOnVideoFragmentListener.onMediaPlayer(playUrl, mMediaPlayer.getCurrentPosition(), mMediaPlayer.getDuration());
        }
    }

    @Override
    public void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mMediaPlayer != null) {
            stop();
            release();
            IjkMediaPlayer.native_profileEnd();
        }
        VideoPlayCache.stop();
        super.onDestroy();
    }

    private IMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1);
//
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100);
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024);//设置缓冲区为100KB，目前我看来，多缓冲了4秒
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 100);// 视频的话，设置100帧即开始播放
//
//        ijkMediaPlayer.setVolume(1.0f, 1.0f);
////
//        setEnableMediaCodec(ijkMediaPlayer, mEnableMediaCodec);
//        mMediaPlayer = new AndroidMediaPlayer();
        mMediaPlayer = ijkMediaPlayer;
        return ijkMediaPlayer;
    }

    //设置是否开启硬解码
    private void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
        int value = isEnable ? 1 : 0;
        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }

    public void setEnableMediaCodec(boolean isEnable) {
        mEnableMediaCodec = isEnable;
    }


    public void mediaPlayer(String url) {
        if (url == null) {
            return;
        }
        showLoading(true);
        stop();
        release();
        createPlayer();
        playUrl = url;
        try {
            mMediaPlayer.setDisplay(mSurfaceHolder);
            String localPath = DownloadVideoManager.getCacheLocalPlayPath(getContext(), url);
            if (new File(localPath).exists()) {
                mMediaPlayer.setDataSource(localPath);
            } else {
                mMediaPlayer.setDataSource(url);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "播放失败", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaPlayer.prepareAsync();
        /**
         * 准备好播放
         */
        mMediaPlayer.setOnPreparedListener(iMediaPlayer -> {
            log("OnPrepared");
            long seek = getSharedPreferences(getContext()).getLong(playUrl, 0);
            mMediaPlayer.seekTo(seek);
            mMediaPlayer.start();
            mSeekBar.setMax(Integer.valueOf(iMediaPlayer.getDuration() + ""));
            showSettingView(false);
            updateTime();

            for (int i = 0; i < movieData.size(); i++) {
                if (movieData.get(i).getUrl().equals(url)) {
                    playPosition = i;
                    return;
                }
            }
        });
        /**
         * 视频参数
         */
        mMediaPlayer.setOnVideoSizeChangedListener((iMediaPlayer, width, height, sarNum, sarDen) -> {
            log("VideoSizeChanged" + width + "----" + height + "----" + sarNum + "----" + sarDen + "----");
            changedSurfaceViewSize(width, height);
        });

        /**
         * 缓冲
         */
        mMediaPlayer.setOnBufferingUpdateListener((iMediaPlayer, i) -> {
            log("BufferingUpdate" + i);
            if (isSettingSeekBar) {
                return;
            }
            if (i != bufIndex) {
                bufIndex = i;
                bufCount = 0f;
            }
            bufCount += 0.05;
            float position = (i + bufCount) / 100 * mSeekBar.getMax();
            mSeekBar.setSecondaryProgress((int) position);
        });
        mMediaPlayer.setOnInfoListener((iMediaPlayer, what, i1) -> {
            log("OnInfoListener" + what + "----" + i1);
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                //缓存中
                showLoading(true);
            }
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END || what == IMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START) {
                //缓存完毕
                showLoading(false);
            }
            return false;
        });
        mMediaPlayer.setOnErrorListener((iMediaPlayer, i, i1) -> {
            log("播放错误");
            return false;
        });
        //播放完成
        mMediaPlayer.setOnCompletionListener(iMediaPlayer -> {
            completion();
            mSeekBar.setProgress(mSeekBar.getMax());
            //播放下一集
            if (movieData.size() > 1) {
                play(movieData.get(playPosition + 1).getUrl());
            }
        });
    }

    //开始加载视频
    private void play(String url) {
        mediaPlayer(url);
    }


    /**
     * 更新播放时间和进度条
     */
    private void updateTime() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                durationTime.post(() -> {
                    if (mMediaPlayer == null) {
                        return;
                    }
                    durationTime.setText(DateUtils.formatterTime(mMediaPlayer.getDuration()));
                    positionTime.setText(DateUtils.formatterTime(mMediaPlayer.getCurrentPosition()));
                    if (!isSettingSeekBar) {
                        mSeekBar.setProgress(Integer.valueOf(mMediaPlayer.getCurrentPosition() + ""));
                    }
                });
            }
        };
        mTimer.schedule(timerTask, 0, 1000);
    }

    /**
     * 加载等待
     */
    private void showLoading(boolean b) {
        mProgressBar.post(() -> mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE));
    }

    private void log(String str) {
        Log.d("VideoFragment", str);
    }

    /**
     * 暂停后 继续播放
     */
    private void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            isPause = false;
            pauseView.setImageResource(R.mipmap.icon_pause);
            updateTime();
        }
        showSettingView(false);
    }

    /**
     * 释放
     */
    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 暂停
     */
    private void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
            pauseView.setImageResource(R.mipmap.icon_start);
            saveCurrentPosition();
        }
    }

    private void completion() {
        if (mMediaPlayer != null) {
            isPause = true;
            pauseView.setImageResource(R.mipmap.icon_start);
            saveCurrentPosition();
            if (mTimer != null) {
                mTimer.cancel();
            }
        }
    }


    private void stop() {
        if (mMediaPlayer != null && isPlaying()) {
            mMediaPlayer.stop();
            saveCurrentPosition();
        }
    }

    /**
     * 保存播放进度
     */
    private void saveCurrentPosition() {
        getSharedPreferences(getContext()).edit().putLong(playUrl, getCurrentPosition()).apply();
    }


    /**
     * 重置
     */
    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }

    /**
     * 播放时长
     *
     * @return
     */
    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    /**
     * 当前播放进度 时间
     *
     * @return
     */
    public long getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    /**
     * 跳转进度
     *
     * @param l
     */
    public void seekTo(long l) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(l);
            start();
        }
    }

    /**
     * 是否在播放
     *
     * @return
     */
    private boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        //触摸主界面
        if (v == rootView) {
            showSettingView(true);
            return;
        }
        int id = v.getId();//返回
        if (id == R.id.backImageView) {
            getActivity().finish();
            //暂停
        } else if (id == R.id.pauseView) {
            if (isPause) {
                start();
            } else {
                pause();
            }
            //锁屏
        } else if (id == R.id.lockView) {
            showSettingView(false);
            isLockView = !isLockView;
            if (isLockView) {
                lockView.setImageResource(R.mipmap.icon_lock_0);
            } else {
                lockView.setImageResource(R.mipmap.icon_lock_1);
            }
            //弹框
        } else if (id == R.id.dialogFrameLayout) {
            if (v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.GONE);
            }
            //选集
        } else if (id == R.id.selectTodTextView) {
            showSettingView(false);
            dialogFrameLayout.setVisibility(View.VISIBLE);
            mTodAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 选集 adapter
     */
    public class TodAdapter extends RecyclerView.Adapter<TodAdapter.TodViewHolder> {

        private List<MovieInfo> data = new ArrayList<>();
        public OnItemClickListener mOnItemClickListener;

        public void setData(List<MovieInfo> urls) {
            if (urls != null) {
                this.data.addAll(urls);
                notifyDataSetChanged();
            }
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public TodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tod_01, parent, false);
            return new TodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TodViewHolder holder, int position) {
            holder.mTextView.setText(data.get(position).getNum() + "");
            if (position == playPosition) {
                holder.mTextView.setTextColor(getResources().getColor(R.color.blue));
            } else {
                holder.mTextView.setTextColor(getResources().getColor(R.color.white));
            }
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(holder, position));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class TodViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextView;

            TodViewHolder(@NonNull View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.text);
            }
        }
    }

    public void onBackPressed() {
        if (dialogFrameLayout.getVisibility() == View.VISIBLE) {
            dialogFrameLayout.setVisibility(View.GONE);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TodAdapter.TodViewHolder viewHolder, int position);
    }

    public interface OnVideoFragmentListener {
        void onMediaPlayer(String url, long currentPosition, long duration);

        void onSettingVisibilityView(boolean b);
    }
}

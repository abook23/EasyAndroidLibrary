package com.abook23.tv.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abook23.tv.R;
import com.android.easy.base.util.DateUtils;

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
    private List<String> urls = new ArrayList<>();
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
    private Timer mTimer = new Timer();
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
    private SharedPreferences mSharedPreferences;

    public final static String VIDEO_PLAY_SHARED_PREFERENCES = "video_play_shared_preferences";
    private String videoName;

    public VideoFragment() {
    }

    public static VideoFragment newInstance() {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private SharedPreferences getSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = getContext().getSharedPreferences(VIDEO_PLAY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
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
            RecyclerView todRecyclerView = rootView.findViewById(R.id.todRecyclerView);
            todRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            todRecyclerView.setAdapter(mTodAdapter = new TodAdapter());
            mTodAdapter.setData(urls);
            SurfaceView surfaceView = rootView.findViewById(R.id.surfaceView);
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
                    positionTime.setText(DateUtils.formatterTime((long) ((float) progress / seekBar.getMax() * mMediaPlayer.getDuration())));
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
            /**
             * 选集
             */
            mTodAdapter.setOnItemClickListener((parent, view, position, id) -> {
                play(urls.get(position));
                dialogFrameLayout.setVisibility(View.GONE);
            });
            if (urls.size() > 0) {
                rootView.findViewById(R.id.selectTodTextView).setVisibility(View.GONE);
            }
        }
        return rootView;
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
            setVisibilityView(b, pauseView/*暂停*/, appBarLinearLayout/*标题等*/, positionTime/*播放时间*/, durationTime/*片长时间*/);
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
            play(playUrl);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
        release();
        IjkMediaPlayer.native_profileEnd();
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
//
//        ijkMediaPlayer.setVolume(1.0f, 1.0f);
////
//        setEnableMediaCodec(ijkMediaPlayer, mEnableMediaCodec);
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

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
        if (mTodAdapter != null) {
            mTodAdapter.setData(urls);
        }
    }

    //开始加载视频
    private void play(String url) {
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
            mMediaPlayer.setDataSource(url);
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
            long seek = getSharedPreferences().getLong(playUrl, 0);
            mMediaPlayer.seekTo(seek);
            mMediaPlayer.start();
            mSeekBar.setMax(Integer.valueOf(iMediaPlayer.getDuration() + ""));
            showSettingView(true);
            updateTime();

            for (int i = 0; i < urls.size(); i++) {
                if (urls.get(i).equals(url)) {
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
        mMediaPlayer.setOnCompletionListener(iMediaPlayer -> {
            //播放下一集
            if (playPosition < urls.size()) {
                play(urls.get(playPosition + 1));
            }
        });
    }

    /**
     * 更新播放时间和进度条
     */
    private void updateTime() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                durationTime.post(() -> {
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
        }
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
        getSharedPreferences().edit().putLong(playUrl, getCurrentPosition()).apply();
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
        switch (v.getId()) {
            //返回
            case R.id.backImageView:
                getActivity().finish();
                break;
            //暂停
            case R.id.pauseView:
                if (isPause) {
                    start();
                } else {
                    pause();
                }
                break;
            //锁屏
            case R.id.lockView:
                showSettingView(false);
                isLockView = !isLockView;
                if (isLockView) {
                    lockView.setImageResource(R.mipmap.icon_lock_0);
                } else {
                    lockView.setImageResource(R.mipmap.icon_lock_1);
                }
                break;
            //弹框
            case R.id.dialogFrameLayout:
                if (v.getVisibility() == View.VISIBLE) {
                    v.setVisibility(View.GONE);
                }
                break;
            //选集
            case R.id.selectTodTextView:
                showSettingView(false);
                dialogFrameLayout.setVisibility(View.VISIBLE);
                mTodAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    /**
     * 选集 adapter
     */
    public class TodAdapter extends RecyclerView.Adapter<TodAdapter.TodViewHolder> {

        private List<String> urls = new ArrayList<>();
        public AdapterView.OnItemClickListener mOnItemClickListener;

        public void setData(List<String> urls) {
            if (urls != null) {
                this.urls.addAll(urls);
                notifyDataSetChanged();
            }
        }

        public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        @NonNull
        @Override
        public TodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tod_1, parent, false);
            return new TodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TodViewHolder holder, int position) {
            holder.mTextView.setText(position + 1 + "");
            if (position == playPosition) {
                holder.mTextView.setTextColor(getResources().getColor(R.color.blue));
            } else {
                holder.mTextView.setTextColor(getResources().getColor(R.color.white));
            }
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(v -> mOnItemClickListener.onItemClick(null, v, position, -1));
            }
        }

        @Override
        public int getItemCount() {
            return urls.size();
        }


        class TodViewHolder extends RecyclerView.ViewHolder {

            TextView mTextView;

            TodViewHolder(@NonNull View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.text);
            }
        }
    }
}
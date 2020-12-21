package com.android.easy.play;

import android.media.AudioManager;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * description:    描述
 * author:         yangxiong
 * e-mail:         abook23@163.com
 * createDate:     2020/12/18 16:03
 * updateUser:     更新者：
 * updateDate:     2020/12/18 16:03
 * updateRemark:   更新说明：
 * version:        1.0
 */
public class MediaPlayerManager {
    private IjkMediaPlayer mMediaPlayer;
    private boolean mEnableMediaCodec;
    private boolean isPause;

    public static MediaPlayerManager getInstance() {
        return new MediaPlayerManager();
    }


    public IjkMediaPlayer createPlayer() {
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

//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 100 * 1024);//设置缓冲区为100KB，目前我看来，多缓冲了4秒
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 100);// 视频的话，设置100帧即开始播放

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);//1变声,0不变声  倍数播放
//        ijkMediaPlayer.setVolume(1.0f, 1.0f);
////
//        setEnableMediaCodec(ijkMediaPlayer, mEnableMediaCodec);
//        mMediaPlayer = new AndroidMediaPlayer();
        mMediaPlayer = ijkMediaPlayer;
        return mMediaPlayer;
    }

    //设置是否开启硬解码
    public void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
        int value = isEnable ? 1 : 0;
        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value);
    }

    public void setEnableMediaCodec(boolean isEnable) {
        mEnableMediaCodec = isEnable;
    }

    /**
     * 暂停后 继续播放
     */
    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放
     */
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    public void stop() {
        if (mMediaPlayer != null && isPlaying()) {
            mMediaPlayer.stop();
        }
    }

    /**
     * 重置
     */
    public void reset() {
        stop();
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
        IjkMediaPlayer.native_profileEnd();
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
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }
}

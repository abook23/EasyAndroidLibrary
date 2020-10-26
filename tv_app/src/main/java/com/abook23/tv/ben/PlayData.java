package com.abook23.tv.ben;

import com.android.easy.play.MovieInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 描述
 * @Author: yangxiong
 * @E-mail: abook23@163.com
 * @CreateDate: 2020/9/16 16:04
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/9/16 16:04
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PlayData implements Serializable {

    private long videoId;
    private String title;
    private String url;
    private List<MovieInfo> mMovieInfos;

    public PlayData(long videoId, String title, String url, List<MovieInfo> movieInfos) {
        this.videoId = videoId;
        this.title = title;
        this.url = url;
        mMovieInfos = movieInfos;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<MovieInfo> getMovieInfos() {
        return mMovieInfos;
    }

    public void setMovieInfos(List<MovieInfo> movieInfos) {
        mMovieInfos = movieInfos;
    }
}

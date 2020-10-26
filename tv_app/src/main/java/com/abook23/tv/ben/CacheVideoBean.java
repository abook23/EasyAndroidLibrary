package com.abook23.tv.ben;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @Description: 描述
 * @Author: yangxiong
 * @E-mail: abook23@163.com
 * @CreateDate: 2020/8/13 21:23
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/8/13 21:23
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
@Entity
public class CacheVideoBean {
    @Id
    public String url;
    public Long v_id;
    public int v_num;
    public long download_progress;
    public long download_max;
    public boolean download_pause;
    public boolean download_complete;
    public String play_rate;
    @Generated(hash = 1542360576)
    public CacheVideoBean(String url, Long v_id, int v_num, long download_progress,
            long download_max, boolean download_pause, boolean download_complete,
            String play_rate) {
        this.url = url;
        this.v_id = v_id;
        this.v_num = v_num;
        this.download_progress = download_progress;
        this.download_max = download_max;
        this.download_pause = download_pause;
        this.download_complete = download_complete;
        this.play_rate = play_rate;
    }
    @Generated(hash = 1547070402)
    public CacheVideoBean() {
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Long getV_id() {
        return this.v_id;
    }
    public void setV_id(Long v_id) {
        this.v_id = v_id;
    }
    public int getV_num() {
        return this.v_num;
    }
    public void setV_num(int v_num) {
        this.v_num = v_num;
    }
    public long getDownload_progress() {
        return this.download_progress;
    }
    public void setDownload_progress(long download_progress) {
        this.download_progress = download_progress;
    }
    public long getDownload_max() {
        return this.download_max;
    }
    public void setDownload_max(long download_max) {
        this.download_max = download_max;
    }
    public boolean getDownload_pause() {
        return this.download_pause;
    }
    public void setDownload_pause(boolean download_pause) {
        this.download_pause = download_pause;
    }
    public boolean getDownload_complete() {
        return this.download_complete;
    }
    public void setDownload_complete(boolean download_complete) {
        this.download_complete = download_complete;
    }
    public String getPlay_rate() {
        return this.play_rate;
    }
    public void setPlay_rate(String play_rate) {
        this.play_rate = play_rate;
    }


}

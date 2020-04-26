package com.abook23.tv.ben;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author abook23@163.com
 *  2019/12/05
 */
@Entity
public class MovieBen implements Serializable {
    /**
     * v_monthtime : 1575360610
     * v_jq : 
     * v_monthhit : 6
     * v_digg : 0
     * v_imdb : 0.0
     * tid : 15
     * v_tags : 
     * v_dayhit : 1
     * v_scorenum : 1
     * v_addtime : 1575360807
     * v_reweek : 
     * v_rank : 0
     * v_gpic : 
     * v_spic : 
     * v_actor : 伊丽莎白·奥尔森,凯莉·玛丽·陈,珍妮·麦克蒂尔,约翰·艾德坡,塔拉·霍尔特,琳登·史密斯
     * v_id : 1
     * v_score : 10
     * v_color : 
     * v_publishyear : 2019
     * v_mtime : 0.0
     * v_tvs : 
     * v_letter : J
     * v_commend : 0
     * v_enname : jieaishunbiandierji
     * v_state : 0
     * v_daytime : 1575470267
     * v_longtxt : 
     * v_note : 10集全
     * v_tread : 0
     * v_money : 0
     * v_wrong : 0
     * v_director : 未知
     * v_douban : 0.0
     * v_lang : 英语
     * v_weektime : 1575360610
     * v_hit : 6
     * v_nickname : 
     * v_publisharea : 美国
     * v_len : 
     * tname : 欧美剧
     * v_total : 
     * v_pic : https://tu.tianzuida.com/pic/upload/vod/2019-10-07/201910071570408362.jpg
     * v_weekhit : 6
     * v_ismake : 0
     * v_ver : 
     * v_topic : 0
     * v_isunion : 0
     * v_company : 
     * v_recycled : 0
     * v_name : 节哀顺变第二季
     */

    private static final long serialVersionUID = 1L;

    public int v_monthtime;
    public String v_jq;
    public int v_monthhit;
    public int v_digg;
    public double v_imdb;
    public int tid;
    public String v_tags;
    public int v_dayhit;
    public int v_scorenum;
    public int v_addtime;
    public String v_reweek;
    public int v_rank;
    public String v_gpic;
    public String v_spic;
    public String v_actor;
    @Id
    public long v_id;
    public int v_score;
    public String v_color;
    public int v_publishyear;
    public double v_mtime;
    public String v_tvs;
    public String v_letter;
    public int v_commend;
    public String v_enname;
    public int v_state;
    public int v_daytime;
    public String v_longtxt;
    public String v_note;
    public int v_tread;
    public int v_money;
    public int v_wrong;
    public String v_director;
    public double v_douban;
    public String v_lang;
    public int v_weektime;
    public int v_hit;
    public String v_nickname;
    public String v_publisharea;
    public String v_len;
    public String tname;
    public String v_total;
    public String v_pic;
    public int v_weekhit;
    public int v_ismake;
    public String v_ver;
    public int v_topic;
    public int v_isunion;
    public String v_company;
    public int v_recycled;
    public String v_name;
    public String body;
    public boolean isCollect;
    public boolean isPlay;

    @Generated(hash = 1576131631)
    public MovieBen(int v_monthtime, String v_jq, int v_monthhit, int v_digg, double v_imdb,
            int tid, String v_tags, int v_dayhit, int v_scorenum, int v_addtime,
            String v_reweek, int v_rank, String v_gpic, String v_spic, String v_actor,
            long v_id, int v_score, String v_color, int v_publishyear, double v_mtime,
            String v_tvs, String v_letter, int v_commend, String v_enname, int v_state,
            int v_daytime, String v_longtxt, String v_note, int v_tread, int v_money,
            int v_wrong, String v_director, double v_douban, String v_lang, int v_weektime,
            int v_hit, String v_nickname, String v_publisharea, String v_len, String tname,
            String v_total, String v_pic, int v_weekhit, int v_ismake, String v_ver,
            int v_topic, int v_isunion, String v_company, int v_recycled, String v_name,
            String body, boolean isCollect, boolean isPlay) {
        this.v_monthtime = v_monthtime;
        this.v_jq = v_jq;
        this.v_monthhit = v_monthhit;
        this.v_digg = v_digg;
        this.v_imdb = v_imdb;
        this.tid = tid;
        this.v_tags = v_tags;
        this.v_dayhit = v_dayhit;
        this.v_scorenum = v_scorenum;
        this.v_addtime = v_addtime;
        this.v_reweek = v_reweek;
        this.v_rank = v_rank;
        this.v_gpic = v_gpic;
        this.v_spic = v_spic;
        this.v_actor = v_actor;
        this.v_id = v_id;
        this.v_score = v_score;
        this.v_color = v_color;
        this.v_publishyear = v_publishyear;
        this.v_mtime = v_mtime;
        this.v_tvs = v_tvs;
        this.v_letter = v_letter;
        this.v_commend = v_commend;
        this.v_enname = v_enname;
        this.v_state = v_state;
        this.v_daytime = v_daytime;
        this.v_longtxt = v_longtxt;
        this.v_note = v_note;
        this.v_tread = v_tread;
        this.v_money = v_money;
        this.v_wrong = v_wrong;
        this.v_director = v_director;
        this.v_douban = v_douban;
        this.v_lang = v_lang;
        this.v_weektime = v_weektime;
        this.v_hit = v_hit;
        this.v_nickname = v_nickname;
        this.v_publisharea = v_publisharea;
        this.v_len = v_len;
        this.tname = tname;
        this.v_total = v_total;
        this.v_pic = v_pic;
        this.v_weekhit = v_weekhit;
        this.v_ismake = v_ismake;
        this.v_ver = v_ver;
        this.v_topic = v_topic;
        this.v_isunion = v_isunion;
        this.v_company = v_company;
        this.v_recycled = v_recycled;
        this.v_name = v_name;
        this.body = body;
        this.isCollect = isCollect;
        this.isPlay = isPlay;
    }

    @Generated(hash = 970439687)
    public MovieBen() {
    }

    
    public int getV_monthtime() {
        return v_monthtime;
    }

    public void setV_monthtime(int v_monthtime) {
        this.v_monthtime = v_monthtime;
    }

    public String getV_jq() {
        return v_jq;
    }

    public void setV_jq(String v_jq) {
        this.v_jq = v_jq;
    }

    public int getV_monthhit() {
        return v_monthhit;
    }

    public void setV_monthhit(int v_monthhit) {
        this.v_monthhit = v_monthhit;
    }

    public int getV_digg() {
        return v_digg;
    }

    public void setV_digg(int v_digg) {
        this.v_digg = v_digg;
    }

    public double getV_imdb() {
        return v_imdb;
    }

    public void setV_imdb(double v_imdb) {
        this.v_imdb = v_imdb;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getV_tags() {
        return v_tags;
    }

    public void setV_tags(String v_tags) {
        this.v_tags = v_tags;
    }

    public int getV_dayhit() {
        return v_dayhit;
    }

    public void setV_dayhit(int v_dayhit) {
        this.v_dayhit = v_dayhit;
    }

    public int getV_scorenum() {
        return v_scorenum;
    }

    public void setV_scorenum(int v_scorenum) {
        this.v_scorenum = v_scorenum;
    }

    public int getV_addtime() {
        return v_addtime;
    }

    public void setV_addtime(int v_addtime) {
        this.v_addtime = v_addtime;
    }

    public String getV_reweek() {
        return v_reweek;
    }

    public void setV_reweek(String v_reweek) {
        this.v_reweek = v_reweek;
    }

    public int getV_rank() {
        return v_rank;
    }

    public void setV_rank(int v_rank) {
        this.v_rank = v_rank;
    }

    public String getV_gpic() {
        return v_gpic;
    }

    public void setV_gpic(String v_gpic) {
        this.v_gpic = v_gpic;
    }

    public String getV_spic() {
        return v_spic;
    }

    public void setV_spic(String v_spic) {
        this.v_spic = v_spic;
    }

    public String getV_actor() {
        return v_actor;
    }

    public void setV_actor(String v_actor) {
        this.v_actor = v_actor;
    }

    public long getV_id() {
        return v_id;
    }

    public void setV_id(long v_id) {
        this.v_id = v_id;
    }

    public int getV_score() {
        return v_score;
    }

    public void setV_score(int v_score) {
        this.v_score = v_score;
    }

    public String getV_color() {
        return v_color;
    }

    public void setV_color(String v_color) {
        this.v_color = v_color;
    }

    public int getV_publishyear() {
        return v_publishyear;
    }

    public void setV_publishyear(int v_publishyear) {
        this.v_publishyear = v_publishyear;
    }

    public double getV_mtime() {
        return v_mtime;
    }

    public void setV_mtime(double v_mtime) {
        this.v_mtime = v_mtime;
    }

    public String getV_tvs() {
        return v_tvs;
    }

    public void setV_tvs(String v_tvs) {
        this.v_tvs = v_tvs;
    }

    public String getV_letter() {
        return v_letter;
    }

    public void setV_letter(String v_letter) {
        this.v_letter = v_letter;
    }

    public int getV_commend() {
        return v_commend;
    }

    public void setV_commend(int v_commend) {
        this.v_commend = v_commend;
    }

    public String getV_enname() {
        return v_enname;
    }

    public void setV_enname(String v_enname) {
        this.v_enname = v_enname;
    }

    public int getV_state() {
        return v_state;
    }

    public void setV_state(int v_state) {
        this.v_state = v_state;
    }

    public int getV_daytime() {
        return v_daytime;
    }

    public void setV_daytime(int v_daytime) {
        this.v_daytime = v_daytime;
    }

    public String getV_longtxt() {
        return v_longtxt;
    }

    public void setV_longtxt(String v_longtxt) {
        this.v_longtxt = v_longtxt;
    }

    public String getV_note() {
        return v_note;
    }

    public void setV_note(String v_note) {
        this.v_note = v_note;
    }

    public int getV_tread() {
        return v_tread;
    }

    public void setV_tread(int v_tread) {
        this.v_tread = v_tread;
    }

    public int getV_money() {
        return v_money;
    }

    public void setV_money(int v_money) {
        this.v_money = v_money;
    }

    public int getV_wrong() {
        return v_wrong;
    }

    public void setV_wrong(int v_wrong) {
        this.v_wrong = v_wrong;
    }

    public String getV_director() {
        return v_director;
    }

    public void setV_director(String v_director) {
        this.v_director = v_director;
    }

    public double getV_douban() {
        return v_douban;
    }

    public void setV_douban(double v_douban) {
        this.v_douban = v_douban;
    }

    public String getV_lang() {
        return v_lang;
    }

    public void setV_lang(String v_lang) {
        this.v_lang = v_lang;
    }

    public int getV_weektime() {
        return v_weektime;
    }

    public void setV_weektime(int v_weektime) {
        this.v_weektime = v_weektime;
    }

    public int getV_hit() {
        return v_hit;
    }

    public void setV_hit(int v_hit) {
        this.v_hit = v_hit;
    }

    public String getV_nickname() {
        return v_nickname;
    }

    public void setV_nickname(String v_nickname) {
        this.v_nickname = v_nickname;
    }

    public String getV_publisharea() {
        return v_publisharea;
    }

    public void setV_publisharea(String v_publisharea) {
        this.v_publisharea = v_publisharea;
    }

    public String getV_len() {
        return v_len;
    }

    public void setV_len(String v_len) {
        this.v_len = v_len;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getV_total() {
        return v_total;
    }

    public void setV_total(String v_total) {
        this.v_total = v_total;
    }

    public String getV_pic() {
        return v_pic;
    }

    public void setV_pic(String v_pic) {
        this.v_pic = v_pic;
    }

    public int getV_weekhit() {
        return v_weekhit;
    }

    public void setV_weekhit(int v_weekhit) {
        this.v_weekhit = v_weekhit;
    }

    public int getV_ismake() {
        return v_ismake;
    }

    public void setV_ismake(int v_ismake) {
        this.v_ismake = v_ismake;
    }

    public String getV_ver() {
        return v_ver;
    }

    public void setV_ver(String v_ver) {
        this.v_ver = v_ver;
    }

    public int getV_topic() {
        return v_topic;
    }

    public void setV_topic(int v_topic) {
        this.v_topic = v_topic;
    }

    public int getV_isunion() {
        return v_isunion;
    }

    public void setV_isunion(int v_isunion) {
        this.v_isunion = v_isunion;
    }

    public String getV_company() {
        return v_company;
    }

    public void setV_company(String v_company) {
        this.v_company = v_company;
    }

    public int getV_recycled() {
        return v_recycled;
    }

    public void setV_recycled(int v_recycled) {
        this.v_recycled = v_recycled;
    }

    public String getV_name() {
        return v_name;
    }

    public void setV_name(String v_name) {
        this.v_name = v_name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean getIsCollect() {
        return this.isCollect;
    }

    public void setIsCollect(boolean isCollect) {
        this.isCollect = isCollect;
    }

    public boolean getIsPlay() {
        return this.isPlay;
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}

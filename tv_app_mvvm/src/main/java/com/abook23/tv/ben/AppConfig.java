package com.abook23.tv.ben;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * author abook23@163.com
 *  2019/12/09
 */
@Entity
public class AppConfig {
    public String name;
    @Id
    public long id;
    public long type;
    public int tid;
    @Generated(hash = 944044085)
    public AppConfig(String name, long id, long type, int tid) {
        this.name = name;
        this.id = id;
        this.type = type;
        this.tid = tid;
    }
    @Generated(hash = 136961441)
    public AppConfig() {
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getType() {
        return this.type;
    }
    public void setType(long type) {
        this.type = type;
    }
    public int getTid() {
        return this.tid;
    }
    public void setTid(int tid) {
        this.tid = tid;
    }
}

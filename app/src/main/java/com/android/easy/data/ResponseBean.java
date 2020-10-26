package com.android.easy.data;

public class ResponseBean<T> {
    public int code;
    public String msg;
    public T data;
    public boolean success;
    public int total;

}

package com.android.easy.retrofit;

public class ResponseBen<T> {
    public int code;
    public boolean success;
    public String msg;
    public T data;
    public int total;
}

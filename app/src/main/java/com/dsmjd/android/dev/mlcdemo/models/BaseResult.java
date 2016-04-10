package com.dsmjd.android.dev.mlcdemo.models;

/**
 * Created by mylitboy on 2016/4/10.
 */
public class BaseResult<T> extends OkResult{
    public int status;
    public   String msg;
    public   T data;
}

package com.hy.frame.mvp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * title MVP中Presenter需要实现的Interface
 * author heyan
 * time 19-8-22 下午3:49
 * desc 无
 */
public interface IBasePresenter {
    @NonNull
    Context getContext();

    @Nullable
    String getString(int strId);
    /**
     * 释放资源
     */
    void destroy();
}

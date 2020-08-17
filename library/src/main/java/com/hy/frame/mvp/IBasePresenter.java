package com.hy.frame.mvp;

import androidx.annotation.Nullable;

/**
 * title MVP中Presenter需要实现的Interface
 * author heyan
 * time 19-8-22 下午3:49
 * desc 无
 */
public interface IBasePresenter {
    /**
     * 请使用GenericLifecycleObserver
     */
    @Nullable
    androidx.lifecycle.LifecycleObserver getLifecycleObserver();

    /**
     * 释放资源
     */
    void onDestroy();
}

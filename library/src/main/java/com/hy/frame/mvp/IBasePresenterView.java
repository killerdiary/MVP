package com.hy.frame.mvp;

import android.support.annotation.Nullable;

/**
 * title MVP中Activity需要实现的Interface
 * author heyan
 * time 19-8-22 下午3:58
 * desc 无
 */
public interface IBasePresenterView<P extends IBasePresenter> {
    @Nullable
    P getPresenter();

    @Nullable
    P buildPresenter();
}

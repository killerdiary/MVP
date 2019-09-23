package com.hy.frame.mvp;

/**
 * title MVP中Activity需要实现的Interface
 * author heyan
 * time 19-8-22 下午3:58
 * desc 无
 */
public interface IBasePresenterView<P extends IBasePresenter> {
    P getPresenter();

    P buildPresenter();
}

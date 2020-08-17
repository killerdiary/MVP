package com.hy.frame.base;


import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;

import com.hy.frame.mvp.IBasePresenter;
import com.hy.frame.mvp.IBasePresenterView;
import com.hy.frame.mvp.IBaseView;

/**
 * title 实现Presenter的BaseFragment
 * author heyan
 * time 19-8-22 下午3:48
 * desc 可以自己相同的方式实现
 */
public abstract class BaseMvpFragment<T extends BaseTemplateUI, P extends IBasePresenter> extends com.hy.frame.base.BaseXFragment<T> implements IBaseView, IBasePresenterView<P> {
    private P mPresenter = null;//如果当前页面逻辑简单, Presenter 可以为 null

    @Override
    @Nullable
    public P getPresenter() {
        if (isIDestroy()) return null;
        if (this.mPresenter == null) {
            this.mPresenter = buildPresenter();
            if (this.mPresenter != null) {
                LifecycleObserver obs = this.mPresenter.getLifecycleObserver();
                if (obs != null)
                    getLifecycle().addObserver(obs);
            }
        }
        if (this.mPresenter == null) this.mPresenter = buildPresenter();
        return this.mPresenter;
    }

    @Override
    @Nullable
    public P buildPresenter() {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.mPresenter != null) {
            this.mPresenter.onDestroy();
            this.mPresenter = null;
        }
    }
}

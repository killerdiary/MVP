package com.hy.frame.mvp;

import android.support.annotation.Nullable;

import com.hy.frame.common.IBaseTemplateUI;
import com.hy.frame.mvp.contract.IBaseContract;
import com.hy.frame.mvp.presenter.BasePresenter;

/**
 * title 实现Presenter的BaseActivity
 * author heyan
 * time 19-8-22 下午3:48
 * desc 可以自己相同的方式实现
 */
public abstract class BaseActivity<P extends IBasePresenter> extends com.hy.frame.ui.simple.BaseActivity implements IBaseView, IBasePresenterView<P> {
    private P mPresenter = null;//如果当前页面逻辑简单, Presenter 可以为 null

    @Override
    @Nullable
    public IBaseTemplateUI getTemplateUI() {
        return this;
    }

    @Override
    @Nullable
    public P getPresenter() {
        if (isIDestroy()) return null;
        if (this.mPresenter == null) this.mPresenter = buildPresenter();
        return this.mPresenter;
    }

    @Override
    @Nullable
    public P buildPresenter() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mPresenter != null) {
            this.mPresenter.destroy();
            this.mPresenter = null;
        }

    }
}

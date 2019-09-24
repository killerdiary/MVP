package com.hy.frame.mvp.presenter;

import android.content.Context;

import com.hy.frame.mvp.IBaseModel;
import com.hy.frame.mvp.IBasePresenter;
import com.hy.frame.mvp.IBaseView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * title BasePresenter
 * author heyan
 * time 19-8-26 上午9:46
 * desc 无
 */
public abstract class BasePresenter<V extends IBaseView, M extends IBaseModel> implements IBasePresenter {
    private Context mContext;
    private V mView;
    private M mModel;

    public BasePresenter(@NonNull Context mContext, @NonNull V mView, @NonNull M mModel) {
        this.mContext = mContext;
        this.mView = mView;
        this.mModel = mModel;
    }

    @NonNull
    protected V getView() {
        return this.mView;
    }

    @NonNull
    protected M getModel() {
        return this.mModel;
    }

    @NonNull
    @Override
    public Context getContext() {
        return this.mContext;
    }

    @Nullable
    @Override
    public String getString(int strId) {
        return getContext().getString(strId);
    }

    @Override
    public void destroy() {
        this.mContext = null;
        this.mView = null;
        this.mModel.destroy();
        this.mModel = null;
    }
}

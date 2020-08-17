package com.hy.frame.mvp.presenter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.hy.frame.mvp.IBaseModel;
import com.hy.frame.mvp.IBasePresenter;
import com.hy.frame.mvp.IBaseView;

/**
 * title BasePresenter
 * author heyan
 * time 19-8-26 上午9:46
 * desc 无
 */
public abstract class BasePresenter<V extends IBaseView, M extends IBaseModel> implements IBasePresenter {

    private V mView;
    private M mModel;
    private LifecycleObserver lifeObserver;

    public BasePresenter(  @NonNull V mView, @NonNull M mModel) {
        this.mView = mView;
        this.mModel = mModel;
        lifeObserver = new LifecycleEventObserver(){

            /**
             * Called when a state transition event happens.
             *
             * @param source The source of the event
             * @param event  The event
             */
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    onDestroy();
                }
            }
        };
    }

    @Nullable
    protected V getView() {
        return this.mView;
    }

    @Nullable
    protected M getModel() {
        return this.mModel;
    }

    @Nullable
    public LifecycleObserver getLifecycleObserver() {
        return lifeObserver;
    }

    @Override
    public void onDestroy() {
        this.mView = null;
        if (this.mModel != null)
            this.mModel.onDestroy();
        this.mModel = null;
    }
}

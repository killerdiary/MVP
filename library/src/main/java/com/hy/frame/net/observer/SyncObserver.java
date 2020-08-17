package com.hy.frame.net.observer;

import androidx.annotation.Nullable;

import java.util.List;

public class SyncObserver<T, E> extends NormalObserver<T> {
    private ISyncCallback<T, E> mListener = null;
    private ISyncCallback<List<T>, E> mListListener = null;

    /**
     * 数据类型
     *
     * @param cls 如果是异步线程 cls = E,否则 = T
     */
    public SyncObserver(Class<T> cls) {
        super(cls);
    }

    public NormalObserver<T> addCallback(ISyncCallback<T, E> listener) {
        this.mListener = listener;
        return this;
    }

    public NormalObserver<T> addListCallback(ISyncCallback<List<T>, E> listener) {
        this.mList = true;
        this.mListListener = listener;
        return this;
    }

    @Override
    public void onExecute(@Nullable Object obj, String msg) {
        E r = null;
        if (mList) {
            if (mListListener != null)
                r = mListListener.onExecute(obj != null ? ((List<T>) obj) : null);
        } else {
            if (mListener != null)
                r = mListener.onExecute(obj != null ? ((T) obj) : null);
        }
        final E r1 = r;
        run(() -> onRunSuccess(r1, msg));
    }

    @Override
    public void onRunSuccess(@Nullable Object obj, String msg) {
        if (mList) {
            if (mListListener != null)
                mListListener.onSuccess(obj != null ? ((E) obj) : null, msg);
        } else {
            if (mListener != null)
                mListener.onSuccess(obj != null ? ((E) obj) : null, msg);
        }
    }

    @Override
    public void onRunError(int errorCode, String msg) {
        if (mList) {
            if (mListListener != null)
                mListListener.onError(errorCode, msg);
        } else {
            if (mListener != null)
                mListener.onError(errorCode, msg);
        }
    }


    /**
     * 支持异步方法
     */
    interface ISyncCallback<C, S> extends ICallback<S> {
        S onExecute(@Nullable C obj);
    }

}

package com.hy.frame.mvp.model;

import com.hy.frame.mvp.IBaseModel;
import com.hy.frame.net.AjaxParams;
import com.hy.frame.net.HttpClient;
import com.hy.frame.net.IHttpClient;
import com.hy.frame.net.IObserver;

/**
 * title 无
 * author heyan
 * time 19-8-26 上午9:36
 * desc 无
 */
public abstract class BaseModel implements IBaseModel {
    private IHttpClient mClient;
    private boolean mDestroy = false;

    @Override
    public IHttpClient getClient() {
        if (!mDestroy) {
            if (mClient == null) mClient = new HttpClient();
            return mClient;
        }
        return null;
    }

    @Override
    public void setLoggable(boolean enable) {
        if (this.getClient() != null) this.getClient().setLoggable(enable);
    }

    @Override
    public void addHeader(String key, String value) {
        if (this.getClient() != null) this.getClient().addHeader(key, value);
    }

    @Override
    public void setApiHost(String apiHost) {
        if (this.getClient() != null) this.getClient().setApiHost(apiHost);
    }

    @Override
    public void destroy() {
        this.mDestroy = true;
        if (this.mClient != null) {
            this.mClient.destroy();
            this.mClient = null;
        }
    }

    @Override
    public void get(String url, IObserver callback) {
        if (this.getClient() != null) this.getClient().get(url, callback);
    }

    @Override
    public void get(String url, AjaxParams params, IObserver callback) {
        if (this.getClient() != null) this.getClient().get(url, params, callback);
    }

    @Override
    public void post(String url, AjaxParams params, IObserver callback) {
        if (this.getClient() != null) this.getClient().post(url, params, callback);
    }

    @Override
    public void upload(String url, AjaxParams params, IObserver callback) {
        if (this.getClient() != null) this.getClient().upload(url, params, callback);
    }

    @Override
    public void download(String url, AjaxParams params, IObserver callback) {
        if (this.getClient() != null) this.getClient().download(url, params, callback);
    }
}

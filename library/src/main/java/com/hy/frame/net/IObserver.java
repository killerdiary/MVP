package com.hy.frame.net;

import okhttp3.ResponseBody;

/**
 * title IHttpCallback
 * author heyan
 * time 19-8-23 上午10:59
 * desc 无
 */
public interface IObserver {
    void onSuccess(ResponseBody body);

    void onError(int errorCode, String msg);
}

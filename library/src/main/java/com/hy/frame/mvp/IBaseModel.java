package com.hy.frame.mvp;

import com.hy.frame.net.IHttpClient;

import android.support.annotation.Nullable;

/**
 * title MVP中Model需要实现的Interface
 * author heyan
 * time 19-8-22 下午3:55
 * desc 无
 */
public interface IBaseModel extends IHttpClient {
    @Nullable
    IHttpClient getClient();

}

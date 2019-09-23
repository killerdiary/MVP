package com.hy.frame.net;

/**
 * title 无
 * author heyan
 * time 19-8-22 下午4:49
 * desc 无
 */
public interface IHttpClient {

    /**
     * 是否开启日志
     */
    void setLoggable(boolean enable);

    /**
     * 添加头信息
     *
     * @param key   键
     * @param value 为空时 移除
     */
    void addHeader(String key, String value);

    /**
     * 设置统一url地址
     */
    void setApiHost(String apiHost);

    /**
     * destroy
     */
    void destroy();

    /**
     * 通用get请求
     *
     * @param url      API地址
     *                 eg: "/api/user/login"
     *                 "http://www.xyz.com/api/user/login"
     * @param callback 回调
     */
    void get(String url, IObserver callback);

    /**
     * 通用get请求
     *
     * @param url      API地址
     *                 eg: "/api/user/login"
     *                 "http://www.xyz.com/api/user/login"
     * @param params   AjaxParams 参数 可空
     * @param callback 回调
     */
    void get(String url, AjaxParams params, IObserver callback);

    /**
     * 通用post请求
     *
     * @param url      API地址 eg: 同上 [get]
     * @param params   参数 不能为空 eg: 同上 [get]
     * @param callback 回调
     */
    void post(String url, AjaxParams params, IObserver callback);


    /**
     * 通用文件上传
     *
     * @param url      API地址 eg: 同上
     * @param params   参数 可空 eg: 同上
     * @param callback 回调
     */
    void upload(String url, AjaxParams params, IObserver callback);


    /**
     * 通用文件下载 GET
     *
     * @param url      API地址 eg: 同上
     * @param params   参数 可空 eg: 同上
     * @param callback 回调
     */
    void download(String url, AjaxParams params, IObserver callback);


}

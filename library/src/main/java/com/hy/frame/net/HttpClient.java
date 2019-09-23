package com.hy.frame.net;

import com.google.gson.Gson;
import com.hy.frame.net.file.Binary;
import com.hy.frame.util.MyLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * title 网络请求
 * author heyan
 * time 19-8-23 下午3:24
 * desc 所有请求使用异步请求, 注意IHttpCallback不在主线程
 */
public class HttpClient implements IHttpClient {
    private String apiHost;
    private boolean mLoggable;
    private boolean isDestroy;
    private OkHttpClient mClient;
    private Map<String, String> mHeaders = null;
    private Set<Call> queues;
    private final static String TAG = "HttpClient";


    @Override
    public void setLoggable(boolean enable) {
        this.mLoggable = enable;
    }

    @Override
    public void addHeader(String key, String value) {
        if (key == null || key.length() == 0) return;
        if (this.mHeaders == null) this.mHeaders = new HashMap<>();
        if (value == null) this.mHeaders.remove(key);
        else this.mHeaders.put(key, value);
    }

    @Override
    public void setApiHost(String apiHost) {
        if (apiHost != null && apiHost.endsWith("/")) {
            this.apiHost = apiHost.substring(0, apiHost.length() - 1);
        } else
            this.apiHost = apiHost;
    }

    private String checkPath(String path) {
        if (path == null || apiHost == null) return path;
        if (path.contains("://")) return path;
        if (path.startsWith("/"))
            return apiHost + path;
        return apiHost + "/" + path;
    }

    private OkHttpClient buildOkHttpClient(AjaxParams params, boolean loggable) {
        int connectTimeout = Const.TIMEOUT_CONNECT;
        int readTimeout = Const.TIMEOUT_READ;
        int writeTimeout = Const.TIMEOUT_WRITE;
        if (params != null) {
            if (params.getConnectTimeout() != 0) connectTimeout = params.getConnectTimeout();
            if (params.getReadTimeout() != 0) readTimeout = params.getReadTimeout();
            if (params.getWriteTimeout() != 0) writeTimeout = params.getWriteTimeout();
        }
        //使用默认client
        if (connectTimeout == Const.TIMEOUT_CONNECT && readTimeout == Const.TIMEOUT_READ && writeTimeout == Const.TIMEOUT_WRITE) {
            if (mClient == null) {
                this.mClient = buildOkHttpClient(connectTimeout, readTimeout, writeTimeout, loggable);
            }
            return this.mClient;
        }
        return buildOkHttpClient(connectTimeout, readTimeout, writeTimeout, loggable);
    }

    private OkHttpClient buildOkHttpClient(int connectTimeout, int readTimeout, int writeTimeout, boolean loggable) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        X509TrustManager trustManager = new SSLUtil.TrustAllManager();
        builder.sslSocketFactory(SSLUtil.createSSLSocketFactory(trustManager), trustManager);
        builder.hostnameVerifier(new SSLUtil.TrustAllHostnameVerifier());
        //builder.callTimeout()
        if (loggable) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String message) {
                    MyLog.d(TAG, message);
                }
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //interceptor.redactHeader("Authorization");
            //interceptor.redactHeader("Cookie");
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }

    /**
     * 构建Request
     *
     * @param method   RequestMethod GET POST
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 请求回调
     * @return Request
     */
    private Request buildRequest(RMethod method, String url, AjaxParams params, IObserver callback) {
        Map<String, String> headers = null;
        int acceptType = Const.ACCEPT_TYPE_JSON;
        String newUrl = url;
        if (params != null) {
            headers = params.getHeaders();
            acceptType = params.getAcceptType();
            if (method == RMethod.GET) {
                StringBuilder sb = new StringBuilder();
                sb.append(url);
                String str = params.getParamsString();
                if (str != null && str.length() > 0) {
                    if (!url.contains("?")) {
                        sb.append("?");
                    } else if (!url.endsWith("?")) {
                        sb.append("&");
                    }
                    sb.append(str);
                }
                newUrl = sb.toString();
            }
        }
        RequestBody body = null;
        if (method != RMethod.GET) {
            body = buildBody(params);
        }
        return buildRequest(method, newUrl, headers, acceptType, body, callback);
    }

    /**
     * 构建Request
     *
     * @param method     RequestMethod GET POST
     * @param url        请求地址
     * @param headers    头信息
     * @param acceptType 响应数据类型
     * @param body       RequestBody 请求数据包
     * @param callback   请求回调
     * @return Request
     */
    private Request buildRequest(RMethod method, String url, @Nullable Map<String, String> headers, int acceptType, RequestBody body, IObserver callback) {
        Request.Builder request = new Request.Builder();
        request.url(checkPath(url));
        request.method(method.toString(), body);
        request.tag(callback);
        //添加默认头信息
        if (mHeaders != null) {
            for (Map.Entry<String, String> map : mHeaders.entrySet()) {
                request.addHeader(map.getKey(), map.getValue());
            }
        }
        //添加自定义头信息
        if (headers != null) {
            for (Map.Entry<String, String> map : headers.entrySet()) {
                request.addHeader(map.getKey(), map.getValue());
            }
        }
        //设置接收的数据类型
        switch (acceptType) {
            case Const.ACCEPT_TYPE_JSON:
            case Const.ACCEPT_TYPE_JSONARRAY:
                request.addHeader(Const.HEAD_KEY_ACCEPT, Const.HEAD_ACCEPT_JSON);
                break;
            case Const.ACCEPT_TYPE_FILE:
                request.addHeader(Const.HEAD_KEY_ACCEPT, Const.HEAD_ACCEPT_FILE);
                break;
            default: //默认 Headers.REQUEST_TYPE_STRING
                request.addHeader(Const.HEAD_KEY_ACCEPT, Const.HEAD_ACCEPT_STRING);
                break;
        }

        return request.build();
    }


    private RequestBody buildBody(AjaxParams params) {
        if (params == null) return null;
        if (params.getObj() != null) {
            Object obj = params.getObj();
            String data = "";
            if (obj instanceof String) {
                //如果是String直接传
                data = (String) obj;
            } else {
                data = new Gson().toJson(obj);
            }
            return FormBody.create(MediaType.parse(Const.CONTENT_TYPE_STRING), data);
        } else if (params.getFiles() != null && params.getFiles().size() > 0) {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            if (params.getParams() != null)
                for (Map.Entry<String, String> map : params.getParams().entrySet()) {
                    builder.addFormDataPart(map.getKey(), map.getValue());
                }
            for (Map.Entry<String, Binary> map : params.getFiles().entrySet()) {
                RequestBody body = RequestBody.create(MediaType.parse(map.getValue().getMimeType()), map.getValue().getFile());
                builder.addFormDataPart(map.getKey(), map.getValue().getFileName(), body);
            }
            return builder.build();
        } else if (params.getParams() != null) {
            FormBody.Builder builder = new FormBody.Builder();
            if (params.getParams() != null)
                for (Map.Entry<String, String> map : params.getParams().entrySet()) {
                    builder.add(map.getKey(), map.getValue());
                }

            return builder.build();
        }
        return null;
    }

    //    public void request(RequestMethod method, String url, Map<String, String> params, Map<String, String> headers, int acceptType, RequestBody body, IHttpCallback callback) {
//        if (callback == null) return;
//        OkHttpClient client = buildOkHttpClient(Const.TIMEOUT_CONNECT, Const.TIMEOUT_READ, Const.TIMEOUT_WRITE, this.mLoggable);
//        Request request = buildRequest(method, url, headers, acceptType, body, callback);
//        request(client, request);
//    }
    private void addQueue(Call call) {
        if (this.queues == null)
            this.queues = new HashSet<>();
        this.queues.add(call);
    }

    private void removeQueue(Call call) {
        if (queues != null) {
            queues.remove(call);
        }
    }

    /**
     * 开始请求 加入队列
     *
     * @param client  OkHttpClient
     * @param request Request
     */
    private void request(OkHttpClient client, Request request) {
//        if (this.mLoggable)
//            MyLog.d(TAG, "request url=" + request.url());
        //开始请求 加入队列
        Call call = client.newCall(request);
        addQueue(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                removeQueue(call);
                String msg = e.getMessage();
                if (mLoggable)
                    MyLog.e(TAG, "onFailure url=" + call.request().url() + ",msg=" + msg);
                IObserver callback = (IObserver) call.request().tag();
                if (callback == null) return;
                callback.onError(-1, msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                removeQueue(call);
//                if (mLoggable)
//                    MyLog.d(TAG, "onResponse url=" + call.request().url());
                if (isDestroy) return;
                IObserver callback = (IObserver) call.request().tag();
                if (callback == null) return;
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), null);
                }
            }

        });
    }

    @Override
    public void destroy() {
        this.isDestroy = true;
        if (this.queues != null) {
            for (Call item : queues) {
                item.cancel();
            }
            this.queues.clear();
            this.queues = null;
        }
        this.mHeaders = null;
        this.mClient = null;
    }

    @Override
    public void get(String url, IObserver callback) {
        get(url, null, callback);
    }

    @Override
    public void get(String url, AjaxParams params, IObserver callback) {
        OkHttpClient client = buildOkHttpClient(params, this.mLoggable);
        Request request = buildRequest(RMethod.GET, url, params, callback);
        request(client, request);
    }

    @Override
    public void post(String url, AjaxParams params, IObserver callback) {
        if (params == null) return;
        OkHttpClient client = buildOkHttpClient(params, this.mLoggable);
        Request request = buildRequest(RMethod.POST, url, params, callback);
        request(client, request);
    }

    @Override
    public void upload(String url, AjaxParams params, IObserver callback) {
        if (params == null) return;
        if (params.getWriteTimeout() == 0) {
            params.setWriteTimeout(60 * 5);//5分钟
        }
        if (params.getReadTimeout() == 0) {
            params.setReadTimeout(60 * 5);//5分钟
        }
        OkHttpClient client = buildOkHttpClient(params, this.mLoggable);
        Request request = buildRequest(RMethod.POST, url, params, callback);
        request(client, request);
    }

    @Override
    public void download(String url, AjaxParams params, IObserver callback) {
        if (params == null) return;
        if (params.getWriteTimeout() == 0) {
            params.setWriteTimeout(60 * 30);//30分钟
        }
        if (params.getReadTimeout() == 0) {
            params.setReadTimeout(60 * 30);//30分钟
        }
        OkHttpClient client = buildOkHttpClient(params, this.mLoggable);
        Request request = buildRequest(RMethod.POST, url, params, callback);
        request(client, request);
    }

}
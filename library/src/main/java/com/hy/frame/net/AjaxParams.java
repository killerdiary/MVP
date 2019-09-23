package com.hy.frame.net;


import com.hy.frame.net.file.Binary;
import com.hy.frame.net.file.FileBinary;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AjaxParams for key/value (include Multipart file)
 *
 * @author panxw
 */
public class AjaxParams {
    private static final String ENCODING = "UTF-8";
    private int acceptType = Const.ACCEPT_TYPE_JSON;
//    private String contentType = Const.ACCEPT_TYPE_JSON;
    private int connectTimeout = Const.TIMEOUT_CONNECT;
    private int readTimeout = Const.TIMEOUT_READ;
    private int writeTimeout = Const.TIMEOUT_WRITE;
    private Map<String, String> mHeaders;
    private Map<String, String> mParams;
    private Map<String, Binary> mFiles;
    private Object obj;

    public AjaxParams() {
    }

    public AjaxParams(String key, String value) {
        put(key, value);
    }

//    private void init() {
//        mParams = new ConcurrentHashMap<>();
//        mFiles = new ConcurrentHashMap<>();
//    }

    //    /**
//     * Add {@link Object} param.
//     *
//     * @param key   param name.
//     * @param value param value.
//     */
//    public AjaxParams put(String key, Object value) {
//        if (key != null && value != null) {
//            put(key, String.valueOf(value));
//        }
//        return this;
//    }

    /**
     * 添加头信息
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams addHeader(String key, String value) {
        if (mHeaders == null) mHeaders = new ConcurrentHashMap<>();
        if (key != null) {
            if (value != null)
                mHeaders.put(key, value);
            else
                mHeaders.remove(key);
        }
        return this;
    }

    /**
     * Add {@link String} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, String value) {
        if (mParams == null) mParams = new ConcurrentHashMap<>();
        if (key != null) {
            if (value != null)
                mParams.put(key, value);
            else
                mParams.remove(key);
        }
        return this;
    }

    /**
     * Add {@link Integer} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, int value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Long} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, long value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Double} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, double value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Float} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, float value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * Add {@link Boolean} param.
     *
     * @param key   param name.
     * @param value param value.
     */
    public AjaxParams put(String key, boolean value) {
        put(key, String.valueOf(value));
        return this;
    }

    /**
     * @param key  param name
     * @param file param value.
     */
    public AjaxParams put(String key, FileBinary file) {
        if (mFiles == null) mFiles = new ConcurrentHashMap<>();
        mFiles.put(key, file);
        return this;
    }

    /**
     * 当使用该字段时 会转换成 json 提交
     *
     * @param obj Object
     */
    public AjaxParams put(Object obj) {
        this.obj = obj;
        return this;
    }

    public int getAcceptType() {
        return acceptType;
    }

    public void setAcceptType(int acceptType) {
        this.acceptType = acceptType;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public Map<String, String> getParams() {
        return mParams;
    }

    public Map<String, Binary> getFiles() {
        return mFiles;
    }

    public Object getObj() {
        return obj;
    }

    public String getParamsString() {
        StringBuilder sb = new StringBuilder();
        if (mParams != null && mParams.size() > 0) {
            int size = mParams.size();
            int i = 0;
            for (Map.Entry<String, String> map : mParams.entrySet()) {
                i++;
                sb.append(map.getKey());
                sb.append("=");
                sb.append(map.getValue());
                if (i < size)
                    sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * 打印日志使用
     */
    public String getFileString() {
        StringBuilder sb = new StringBuilder();
        if (mFiles != null && mFiles.size() > 0) {
            int size = mFiles.size();
            int i = 0;
            for (Map.Entry<String, Binary> map : mFiles.entrySet()) {
                i++;
                sb.append(map.getKey());
                sb.append("=");
                sb.append(map.getValue());
                if (i < size)
                    sb.append("&");
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getParamsString());
        String fileParamsString = getFileString();
        if (sb.length() > 0 && fileParamsString != null && fileParamsString.length() > 0) {
            sb.append("&");
            sb.append(fileParamsString);
        }
        return sb.toString();
    }
}
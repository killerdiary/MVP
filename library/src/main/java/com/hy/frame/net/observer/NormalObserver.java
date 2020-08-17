package com.hy.frame.net.observer;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hy.frame.net.IObserver;
import com.hy.frame.util.FormatUtil;
import com.hy.frame.util.JsonUtil;
import com.hy.frame.util.LogUtil;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * title NormalObserver
 * author heyan
 * time 19-8-23 下午3:31
 * desc T 为 需要的结果 , E 异步方法里的数据类型
 */
public class NormalObserver<T> implements IObserver {

    private ICallback<T> mListener = null;
    private ICallback<List<T>> mListListener = null;
    protected Class<T> cls = null;
    protected boolean isTemplate = true;
    protected String tCode = "code";
    protected String tMsg = "msg";
    protected String tData = "data";
    protected int tCodeSucces = 1;
    protected boolean mList;
    protected Handler mHandler;
    private final static String TAG = "NormalObserver";

    /**
     * 数据类型
     *
     * @param cls 如果是异步线程 cls = E,否则 = T
     */
    public NormalObserver(Class<T> cls) {
        this.cls = cls;
    }

    /**
     * 添加回调
     *
     * @param listener 回调
     */
    public NormalObserver<T> addCallback(ICallback<T> listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * 添加回调
     *
     * @param listener 回调
     */
    public NormalObserver<T> addListCallback(ICallback<List<T>> listener) {
        this.mList = true;
        this.mListListener = listener;
        return this;
    }

    /**
     * 不使用模板
     */
    public NormalObserver<T> noTemplate() {
        this.isTemplate = false;
        return this;
    }

    /**
     * 使用模板
     *
     * @param code        错误码KEY
     * @param msg         描述文本KEY
     * @param data        数据KEY
     * @param codeSuccess 成功码
     */
    public NormalObserver<T> template(@NonNull String code, @NonNull String msg, @NonNull String data, int codeSuccess) {
        this.isTemplate = true;
        this.tCode = code;
        this.tCodeSucces = codeSuccess;
        this.tData = data;
        this.tMsg = msg;
        return this;
    }


    /**
     * 结果数据是list类型,使用模板表示data对应json数据是[]
     */
    public NormalObserver<T> list() {
        this.mList = true;
        return this;
    }

    @Override
    public void onSuccess(ResponseBody body) {
        if (body == null) return;
        int code = -1;//0异常 1成功
        String msg = "";
        // 1. 通用模板
        String data = null;
        try {
            data = body.string();
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "IOException");
        }
        //LogUtil.d(TAG, "onSuccess " + data);
        if (mListener == null && mListListener == null) {
            LogUtil.e(TAG, "未配置listener");
            return;
        }
        if (cls == null) {
            LogUtil.e(TAG, "未配置 对象类型Class");
            msg = "程序配置错误";
            onError(code, msg);
            return;
        }
        Object any = null;
        if (data == null || data.length() == 0) {
            LogUtil.e(TAG, "服务器无响应 数据为空");
            msg = "网络繁忙,没有响应";
            onError(code, msg);
            return;
        }
        try {
            if (!isTemplate) {
                code = tCodeSucces;
                if (mList) {
                    //是List
                    JsonElement element = new JsonParser().parse(data);
                    if (element.isJsonArray()) {
                        any = JsonUtil.getListFromJson(element, cls);
                    }
                } else {
                    //直接转换
                    if (cls == String.class) {
                        any = data;
                    } else if (cls == Boolean.class || cls == boolean.class) {
                        any = Boolean.parseBoolean(data);
                    } else if (cls == Integer.class || cls == int.class) {
                        any = Integer.parseInt(data);
                    } else if (cls == Double.class || cls == double.class) {
                        any = Double.parseDouble(data);
                    } else if (cls == Float.class || cls == float.class) {
                        any = Float.parseFloat(data);
                    } else if (cls == Long.class || cls == long.class) {
                        any = Long.parseLong(data);
                    } else {
                        JsonElement element = new JsonParser().parse(data);
//                        if (element.isJsonObject()) {
//                            any = new Gson().fromJson(element, cls);
//                        }
                        //强制转换
                        any = new Gson().fromJson(element, cls);
                    }
                }
            } else {
                //使用模板
                JsonElement json = new JsonParser().parse(data);
                if (json.isJsonObject()) {
                    JsonObject obj = json.getAsJsonObject();
                    if (obj.has(tCode)) {
                        code = obj.get(tCode).getAsInt();
                    }
                    if (obj.has(tMsg)) {
                        msg = obj.get(tMsg).getAsString();
                    }
                    if (code == tCodeSucces) {
                        if (obj.has(tData)) {
                            JsonElement element = obj.get(tData);
                            if (mList) {
                                //是List
                                if (element.isJsonArray()) {
                                    any = JsonUtil.getListFromJson(element, cls);
                                }
                            } else {
                                //是Object
                                if (element.isJsonObject()) {
                                    any = JsonUtil.getObjectFromJson(element, cls);
                                } else if (element.isJsonPrimitive()) {
                                    if (cls == String.class) {
                                        any = element.getAsString();
                                    } else if (cls == Boolean.class || cls == boolean.class) {
                                        any = element.getAsBoolean();
                                    } else if (cls == Integer.class || cls == int.class) {
                                        any = element.getAsInt();
                                    } else if (cls == Double.class || cls == double.class) {
                                        any = element.getAsDouble();
                                    } else if (cls == Float.class || cls == float.class) {
                                        any = element.getAsFloat();
                                    } else if (cls == Long.class || cls == long.class) {
                                        any = element.getAsLong();
                                    }
                                } else {
                                    //强制转换
                                    any = new Gson().fromJson(element, cls);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "数据异常 " + e.toString());
            code = -1;
            msg = "网络繁忙，请稍后重试";
        }
        if (code == tCodeSucces) {
            onExecute(any, msg);
            return;
        }
        onError(code, msg);
    }

    public void onExecute(@Nullable Object obj, String msg) {
        run(() -> onRunSuccess(obj, msg));
    }

    @Override
    public void onError(int errorCode, String msg) {
        run(() -> onRunError(errorCode, getErrorMsg(errorCode, msg)));
    }

    public void onRunSuccess(@Nullable Object obj, String msg) {
        if (mList) {
            if (mListListener != null)
                mListListener.onSuccess(obj != null ? ((List<T>) obj) : null, msg);
        } else {
            if (mListener != null)
                mListener.onSuccess(obj != null ? ((T) obj) : null, msg);
        }
    }

    public void onRunError(int errorCode, String msg) {
        if (mList) {
            if (mListListener != null)
                mListListener.onError(errorCode, msg);
        } else {
            if (mListener != null)
                mListener.onError(errorCode, msg);
        }
    }

    private String getErrorMsg(int errorCode, String msg) {
        if (FormatUtil.isNoEmpty(msg)) return msg;
        if (errorCode == -2)
            return "请检查网络，稍后重试";
        return "请求失败，请稍后重试";
    }


    private Handler getHandler() {
        if (this.mHandler == null)
            this.mHandler = new Handler(Looper.getMainLooper());
        return this.mHandler;
    }

    protected void run(@NonNull Runnable runnable) {
        //切换主线程 不用判断
        getHandler().post(runnable);
    }

    public interface ICallback<T> {

        void onSuccess(@Nullable T obj, @Nullable String msg);

        void onError(int errorCode, @Nullable String msg);
    }

    /**
     * 支持异步方法
     *
     * @param <E>
     * @param <T>
     */
    public interface ISyncCallback<E, T> extends ICallback<T> {

        T onExecute(@Nullable E obj);
    }
}

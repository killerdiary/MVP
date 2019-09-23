package com.hy.frame.net.observer;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hy.frame.net.IObserver;
import com.hy.frame.util.FormatUtil;
import com.hy.frame.util.JsonUtil;
import com.hy.frame.util.MyLog;


//import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * title NormalObserver
 * author heyan
 * time 19-8-23 下午3:31
 * desc T 为 需要的结果 , E 异步方法里的数据类型
 */
public class NormalObserver implements IObserver {

    private ICallback mListener = null;
    private Class<?> cls = null;
    private boolean isTemplate = true;
    private String tCode = "code";
    private String tMsg = "msg";
    private String tData = "data";
    private int tCodeSucces = 1;
    private boolean mObserveSyncThread;
    private boolean mList;
    private Handler mHandler;
    private final static String TAG = "NormalObserver";

    /**
     * 数据类型
     *
     * @param cls 如果是异步线程 cls = E,否则 = T
     */
    public NormalObserver(Class<?> cls) {
        this.cls = cls;
    }

    /**
     * 添加回调
     *
     * @param listener 回调
     */
    public NormalObserver addCallback(ICallback listener) {
        if (listener instanceof ISyncCallback) {
            this.mObserveSyncThread = true;
        }
        this.mListener = listener;
        return this;
    }

    /**
     * 不使用模板
     */
    public NormalObserver noTemplate() {
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
    public NormalObserver template(@NotNull String code, @NotNull String msg, @NotNull String data, int codeSuccess) {
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
    public NormalObserver list() {
        this.mList = true;
        return this;
    }


    /**
     * 结果反馈到异步线程
     * 1. 成功处理数据后,会优先反馈到异步线程方法 ICallback.onExecute
     * 2. 然后把 onExecute 处理结果 反馈到ICallback.onSuccess
     */
    public NormalObserver observeSyncThread() {
        this.mObserveSyncThread = true;
        return this;
    }

    @Override
    public void onSuccess(ResponseBody body) {
        int code = 0;//0异常 1成功
        String msg = "";
        // 1. 通用模板
        String data = null;
        try {
            data = body.string();
        } catch (IOException e) {
            e.printStackTrace();
            MyLog.e(TAG, "IOException");
        }
        //MyLog.d(TAG, "onSuccess " + data);
        if (mListener == null) {
            MyLog.e(TAG, "未配置listener");
            return;
        }
        if (cls == null) {
            MyLog.e(TAG, "未配置 对象类型Class");
            msg = "程序配置错误";
            onError(code, msg);
            return;
        }
        Object any = null;
        if (data == null || data.length() == 0) {
            MyLog.e(TAG, "服务器无响应 数据为空");
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
                        any = Boolean.valueOf(data);
                    } else if (cls == Integer.class || cls == int.class) {
                        any = Integer.valueOf(data);
                    } else if (cls == Double.class || cls == double.class) {
                        any = Double.valueOf(data);
                    } else if (cls == Float.class || cls == float.class) {
                        any = Float.valueOf(data);
                    } else if (cls == Long.class || cls == long.class) {
                        any = Long.valueOf(data);
                    } else {
                        JsonElement element = new JsonParser().parse(data);
                        if (element.isJsonObject()) {
                            any = new Gson().fromJson(element, cls);
                        }
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
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyLog.e(TAG, "数据异常 " + e.toString());
            code = -1;
            msg = "网络繁忙，请稍后重试";
        }
        if (code == tCodeSucces) {
            if (this.mObserveSyncThread) {
                //成功
                onExecute(any, msg);
            } else {
                onSuccess(any, msg);
            }
            return;
        }
        onError(code, msg);
    }

    private void onExecute(@Nullable Object obj, String msg) {
        if (mListener != null && mListener instanceof ISyncCallback) {
            ISyncCallback listener = (ISyncCallback) mListener;
            Object r = listener.onExecute(obj);
            onSuccess(r, msg);
        }
    }

    //@Suppress("UNCHECKED_CAST")
    //@SuppressWarnings("UNCHECKED_CAST")
    private void onSuccess(@Nullable Object obj, String msg) {
        run(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onSuccess(obj, msg);
                }
            }
        });


    }

    @Override
    public void onError(int errorCode, String msg) {
        run(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onError(errorCode, getErrorMsg(errorCode, msg));
                }
            }
        });
    }

    private String getErrorMsg(int errorCode, String msg) {
        if (FormatUtil.isNoEmpty(msg)) return msg;
        return "网络繁忙，请稍后重试";
    }

    private Handler getHandler() {
        if (this.mHandler == null)
            this.mHandler = new Handler(Looper.getMainLooper());
        return this.mHandler;
    }

    private void run(@NotNull Runnable runnable) {
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

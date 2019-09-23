package com.hy.frame.net.observer;

import android.os.Handler;
import android.os.Looper;

import com.hy.frame.net.IObserver;
import com.hy.frame.util.FormatUtil;
import com.hy.frame.util.MyLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import okhttp3.ResponseBody;

/**
 * title 文件下载 非主线程
 * author heyan
 * time 18-12-4 上午11:23
 * desc 包含断点限速功能
 */
public class FileObserver implements IObserver {

    private ICallback mListener = null;
    private String mSavePath;
    private boolean mNeedProgress;
    private boolean mRenewal;
    private long mLimitSpeed;
    //    private boolean mObserveMainThread;
    private Handler mHandler;

    private final static String TAG = "FileObserver";
    public final static long TIME_NOTIFY = 200L; //通知进度间隔
    public final static long THREAD_SPLEEP = 100L;//限速间隔
    public final static String EXT_TEMP = "c";
    public final static int STATUS_START = 0;
    public final static int STATUS_PROGRESS = 1;
    public final static int STATUS_SUCCESS = 2;
    public final static int STATUS_ERROR = -1;

    /**
     * 初始化
     *
     * @param listener 回调
     * @param savePath 保存路径
     */
    public FileObserver(ICallback listener, String savePath) {
        this.mListener = listener;
        this.mSavePath = savePath;
    }

    /**
     * 初始化
     *
     * @param listener       回调
     * @param savePath       保存路径
     * @param isNeedProgress 是否反馈下载进度，此时onSuccess中mstate==STATUS_PROGRESS
     * @param renewal        是否开启断点下载功能
     * @param limitSpeed     每秒限制速度(单位B)，0时不限速
     */
    public FileObserver(ICallback listener, String savePath, boolean isNeedProgress, boolean renewal, long limitSpeed) {
        this.mListener = listener;
        this.mSavePath = savePath;
        this.mNeedProgress = isNeedProgress;
        this.mRenewal = renewal;
        this.mLimitSpeed = limitSpeed;
    }

//    /**
//     * 结果反馈到主线程
//     */
//    public FileObserver observeMainThread() {
//        this.mObserveMainThread = true;
//        return this;
//    }

    @Override
    public void onSuccess(ResponseBody body) {
        MyLog.d(TAG, "onSuccess ");
        int code = 0;//0异常 1成功
        String msg = "";
        if (mListener == null) {
            MyLog.e(TAG, "未配置listener");
            return;
        }
        if (mSavePath == null || mSavePath.length() == 0) {
            MyLog.e(TAG, "未配置文件存储路径");
            msg = "程序配置错误";
            onError(code, msg);
            return;
        }
        DownFile dFile = new DownFile();
        dFile.setFilePath(mSavePath);
        dFile.setState(STATUS_START);
        File file = new File(mSavePath);
        File tempFile = new File(mSavePath + EXT_TEMP);
        long tempSize = 0L;
        if (mRenewal && tempFile.exists()) {
            tempSize += tempFile.length();
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[1024]; //1024b = 1kb
            long fileSize = body.contentLength();
            long downloadSize = 0;
            dFile.setState(STATUS_PROGRESS);
            dFile.setFileSize(fileSize + tempSize);
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(tempFile, this.mRenewal);
            int read;
            long downloadCache = 0L;
            long time;
            long lastNotifyTime = 0L;
            while (true) {
                read = inputStream.read(fileReader);
                if (read == -1) {
                    break;//读取完 跳出
                }
                outputStream.write(fileReader, 0, read);
                downloadSize += read;
                downloadCache += read;
                dFile.setDownloadSize(downloadSize + tempSize);
                time = System.currentTimeMillis();
                //进度反馈
                if (mNeedProgress && time - lastNotifyTime >= TIME_NOTIFY) {
                    lastNotifyTime = time;
                    onProgress(dFile, "downloaded");
                }
                //限速 必须大于1kb
                if (mLimitSpeed > 1000L) {
                    if (downloadCache >= mLimitSpeed / 10) {
                        downloadCache = 0;
                        Thread.sleep(THREAD_SPLEEP);
                    }
                }
            }
            outputStream.flush();
            dFile.setState(STATUS_SUCCESS);//下载完成
            tempFile.renameTo(file);//重命名缓存文件
            MyLog.d(TAG, String.format(Locale.CHINA, "onSuccess path=%s %s/%s", mSavePath, dFile.downloadSize, dFile.fileSize));
            onSuccess(dFile, "下载完成");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        onError(code, msg);
    }

    private void onProgress(DownFile obj, String msg) {
        run(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onSuccess(obj, msg);
                }
            }
        });
    }

    //@Suppress("UNCHECKED_CAST")
    //@SuppressWarnings("UNCHECKED_CAST")
    private void onSuccess(DownFile obj, String msg) {
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

    private void run(Runnable runnable) {
//        if (mObserveMainThread) {
        //切换主线程 不用判断
        getHandler().post(runnable);
//        } else {
//            runnable.run();
//        }
    }

    interface ICallback {
        void onSuccess(FileObserver.DownFile obj, String msg);

        void onError(int errorCode, String msg);
    }

    /**
     * title 下载文件信息
     * author heyan
     * time 19-8-23 下午5:19
     * desc 无
     */
    public class DownFile {
        private String url = null;
        private String filePath; //文件路径
        private int state; //下载状态
        private long fileSize; //文件总大小
        private long downloadSize; //当前文件大小

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public long getDownloadSize() {
            return downloadSize;
        }

        public void setDownloadSize(long downloadSize) {
            this.downloadSize = downloadSize;
        }

        /**
         * 获取进度 小数
         */
        public float getProgress() {
            if (fileSize > 0 && downloadSize > 0) {
                if (downloadSize >= fileSize) {
                    return 1;
                }
                return ((float) downloadSize) / fileSize;
            }
            return 0;
        }
    }

}

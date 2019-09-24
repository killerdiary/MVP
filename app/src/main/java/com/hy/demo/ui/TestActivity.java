package com.hy.demo.ui;

import android.os.Message;
import android.view.View;

import com.hy.demo.app.BaseActivity;
import com.hy.demo.mvp.R;
import com.hy.frame.util.MyHandler;

/**
 * title 无
 * author heyan
 * time 19-7-11 下午3:27
 * desc 无
 */
public class TestActivity extends BaseActivity {
    private MyHandler handler = null;

    @Override
    public int getLayoutId() {
        return R.layout.v_main;
    }

    @Override
    public void initView() {
        initHeader(android.R.drawable.ic_menu_revert, R.string.appName, R.string.confirm);
        getTemplateController().showLoading("测试...");
    }

    @Override
    public void initData() {

        handler = new MyHandler(getCurContext(), new MyHandler.HandlerListener() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    getTemplateController().showCView();
                } else {
                    getTemplateController().hideLoadingDialog();
                }
            }
        });
        handler.sendEmptyMessageDelayed(0, 3500L);
    }

    @Override
    public void onViewClick(View v) {

    }

    @Override
    public void onRightClick() {
        getTemplateController().showLoadingDialog("提交中 ....");
        handler.sendEmptyMessageDelayed(1, 3500L);
    }
}

package com.hy.demo.contract;

import android.support.annotation.NonNull;

import com.hy.frame.mvp.contract.IBaseContract;

/**
 * title TestContract
 * author heyan
 * time 19-9-27 上午9:49
 * desc 无
 */
interface TestContract {
    interface IView extends IBaseContract.IView {
        void requestSuccess(String data);

        void requestError(@NonNull String msg);
    }

    interface IModel extends IBaseContract.IModel {
    }

    interface IPresenter extends IBaseContract.IPresenter {
        void requestData();
    }
}

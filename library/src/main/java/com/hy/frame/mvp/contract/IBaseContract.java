package com.hy.frame.mvp.contract;

/**
 * title 无
 * author heyan
 * time 19-8-22 下午3:45
 * desc 无
 */
public interface IBaseContract {
    interface IView extends com.hy.frame.mvp.IBaseView {
    }

    interface IModel extends com.hy.frame.mvp.IBaseModel {
    }

    interface IPresenter extends com.hy.frame.mvp.IBasePresenter{
    }
}

package com.hy.frame.mvp;

import com.hy.frame.common.IBaseTemplateUI;

/**
 * title MVP中View需要实现的Interface
 * author heyan
 * time 19-8-22 下午3:48
 * desc 无
 */
public interface IBaseView {
    /**
     * 获取模板[com.hy.frame.common.IBaseTemplateUI]
     */
    IBaseTemplateUI getTemplateUI();
}

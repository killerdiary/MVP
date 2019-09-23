package com.hy.demo.ui.list;

import android.view.View;
import android.widget.ListView;

import com.hy.demo.adapter.ListAdapter;
import com.hy.demo.app.BaseActivity;
import com.hy.demo.iframe.R;
import com.hy.frame.adapter.IAdapterListener;

import java.util.ArrayList;
import java.util.List;

/**
 * title 无
 * author heyan
 * time 19-7-11 下午3:27
 * desc 无
 */
public class ListActivity extends BaseActivity implements IAdapterListener<String> {
    private ListView cList;
    private List<String> datas;
    private ListAdapter adapter;


    @Override
    public int getLayoutId() {
        return R.layout.v_list_list;
    }

    @Override
    public void initView() {
        cList = findViewById(R.id.list_list_cList);
    }

    @Override
    public void initData() {
        initHeader(android.R.drawable.ic_menu_revert, R.string.menu_list_list);
        requestData();
    }


    public void requestData() {
        datas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            datas.add("测试" + i);
        }
        updateUI();
    }

    public void updateUI() {
        if (adapter == null) {
            adapter = new ListAdapter(getCurContext(), datas, this);
            cList.setAdapter(adapter);
        } else
            adapter.refresh(datas);
    }

    @Override
    public void onViewClick(View v) {

    }

    @Override
    public void onViewClick(View v, String item, int position) {
        getTemplateController().showToast(item);

    }

}

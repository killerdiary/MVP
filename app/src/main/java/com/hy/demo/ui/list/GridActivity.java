package com.hy.demo.ui.list;

import android.view.View;
import android.widget.GridView;
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
public class GridActivity extends BaseActivity implements IAdapterListener<String> {
    private GridView cGrid;
    private List<String> datas;
    private ListAdapter adapter;


    @Override
    public int getLayoutId() {
        return R.layout.v_list_grid;
    }

    @Override
    public void initView() {
        cGrid = findViewById(R.id.list_grid_cGrid);
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
            cGrid.setAdapter(adapter);
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

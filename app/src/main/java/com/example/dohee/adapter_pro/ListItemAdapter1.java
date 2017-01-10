package com.example.dohee.adapter_pro;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dohee on 16. 12. 5.
 */

/* 연락처리스트 어댑터 */
public class ListItemAdapter1 extends BaseAdapter {

    private Context mContext;
    private List<TextItem> mItems = new ArrayList<TextItem>();

    public ListItemAdapter1(Context context) {
        mContext = context;
    }

    public void addItem(TextItem it) {
        mItems.add(it);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ListItemView1 itemView = null;

        if (view == null) {
            itemView = new ListItemView1(mContext, mItems.get(i));
        } else {
            itemView = (ListItemView1)view;
            itemView.setText(0, mItems.get(i).getData(0));
            itemView.setText(1, mItems.get(i).getData(1));
        }

        return itemView;
    }
}

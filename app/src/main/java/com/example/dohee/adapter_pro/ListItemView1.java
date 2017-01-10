package com.example.dohee.adapter_pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by dohee on 16. 12. 5.
 */

/* 연락처리스트뷰 */
public class ListItemView1 extends LinearLayout{

    private TextView mText01, mText02;

    public ListItemView1(Context context, TextItem aItem) {

        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem1, this, true);

        mText01 = (TextView) findViewById(R.id.name);
        mText01.setText(aItem.getData(0));

        mText02 = (TextView) findViewById(R.id.number);
        mText02.setText(aItem.getData(1));

    }

    public void setText(int index, String data) {
        if (index == 0)
            mText01.setText(data);
        else if (index == 1)
            mText02.setText(data);
        else
            throw new IllegalArgumentException();
    }
}

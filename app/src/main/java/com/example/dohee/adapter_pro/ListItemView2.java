package com.example.dohee.adapter_pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by dohee on 16. 12. 5.
 */

/* 일정리스트뷰 */
public class ListItemView2 extends LinearLayout{

    private TextView mText01, mText02, mText03, mText04;

    public ListItemView2(Context context, TextItem aItem) {

        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listitem2, this, true);

        mText01 = (TextView) findViewById(R.id.date);
        mText01.setText(aItem.getData(0));

        mText02 = (TextView) findViewById(R.id.time);
        mText02.setText(aItem.getData(1));

        mText03 = (TextView) findViewById(R.id.task);
        mText03.setText(aItem.getData(2));

        mText04 = (TextView) findViewById(R.id.name);
        mText04.setText(aItem.getData(2));

    }

    public void setText(int index, String data) {
        if (index == 0)
            mText01.setText(data);
        else if (index == 1)
            mText02.setText(data);
        else if(index == 2)
            mText03.setText(data);
        else if(index == 3)
            mText04.setText(data);
        else
            throw new IllegalArgumentException();
    }
}

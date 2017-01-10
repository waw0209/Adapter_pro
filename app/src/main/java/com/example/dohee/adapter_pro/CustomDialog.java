package com.example.dohee.adapter_pro;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class CustomDialog extends Dialog {


    private Button mLeftButton, mRightButton;
    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    public CustomDialog(Context context,
                        View.OnClickListener leftListener,
                        View.OnClickListener rightListener) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);


        this.mLeftClickListener = leftListener;
        this.mRightClickListener = rightListener;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.7f;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
        setContentView(R.layout.activity_dialog);


        mLeftButton = (Button)findViewById(R.id.send);
        mRightButton = (Button)findViewById(R.id.cancel);

        if(mLeftClickListener != null && mRightClickListener != null){
            mLeftButton.setOnClickListener(mLeftClickListener);
            mRightButton.setOnClickListener(mRightClickListener);
        }
    }
}

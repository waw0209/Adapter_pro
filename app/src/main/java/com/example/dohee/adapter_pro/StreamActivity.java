package com.example.dohee.adapter_pro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class StreamActivity extends AppCompatActivity {


    Button button;
    WebView web;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);


        button = (Button)findViewById(R.id.button2);
        web = (WebView)findViewById(R.id.web);          // webView 사이즈 조정

        web.setVisibility(web.VISIBLE);
        web.setBackgroundColor(0);
        web.getSettings().setJavaScriptEnabled(true);

        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView v, String s) {
                return super.shouldOverrideUrlLoading(v, s);
            }
        });
        web.loadUrl("http://222.112.247.133:8080/stream");
        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


    }

    public void restart(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.button2:

                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                break;

        }

    }

}
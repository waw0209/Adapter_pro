package com.example.dohee.adapter_pro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LoginActivity extends AppCompatActivity {

    Button btn1, btn2;
    EditText edit1, edit2;
    CheckBox checkBox;
    TextView textView;
    boolean loginChecked; // 자동로그인체크유무.
    String input1, input2; // 아이디, 비밀번호입력.

    String id, pw;

    SharedPreferences prefs; // 개인정보저장 preferences
    SharedPreferences.Editor editor;

    private Context context = LoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        edit1 = (EditText)findViewById(R.id.edit1);
        edit2 = (EditText)findViewById(R.id.edit2);
        checkBox = (CheckBox)findViewById(R.id.checkbox);
        textView = (TextView)findViewById(R.id.textview);

        btn1.setOnClickListener(onClickListener1);
        btn2.setOnClickListener(onClickListener1);
        edit1.setOnClickListener(onClickListener2);
        edit2.setOnClickListener(onClickListener2);
        checkBox.setOnCheckedChangeListener(onCheckedChangeListener);

        /* 로그인정보가 담긴 preferences */
        prefs = getSharedPreferences("MyInfo", MODE_PRIVATE);
        editor = prefs.edit();


        /* 자동로그인표시가 되어있을때, 저장된 아이디와 비밀번호를 가져와서 입력합니다. */
        if(prefs.getBoolean("autoLogin", false)) {
            edit1.setText(prefs.getString("id", ""));
            edit2.setText(prefs.getString("pw", ""));
            checkBox.setChecked(true);
        }
    }

    /* 로그인과 회원가입버튼 기능 */
    View.OnClickListener onClickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn1: // 로그인하기위해 서버에 접속
                    id = edit1.getText().toString();
                    pw = edit2.getText().toString();
                    CheckLogin c = new CheckLogin();
                    c.execute(id, pw);
                    break;
                case R.id.btn2: // 회원가입창으로 이동.
                    Intent intent = new Intent(context, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        }

    };

    /* textView에 오류메세지가 출력되어있을 경우에 초기화 */
    View.OnClickListener onClickListener2 = new View.OnClickListener(){

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.edit1:
                    textView.setText("");
                    break;
                case R.id.edit2:
                    textView.setText("");
                    break;
            }
        }
    };

    /* 자동로그인 수락유무 */
    CheckBox.OnCheckedChangeListener onCheckedChangeListener = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

            input1 = edit1.getText().toString();
            input2 = edit2.getText().toString();

            if(isChecked) { // 체크가 되어있으면 입력한 내용저장
                loginChecked = true;
                editor.putString("id", input1);
                editor.putString("pw", input2);
                editor.putBoolean("autoLogin", loginChecked);
                editor.commit();
            }
            else{ // 체크가 되어있지않으면 내용삭제
                loginChecked = false;
                editor.clear();
                editor.commit();
            }
        }
    };

    /* 로그인 thread */
    class CheckLogin extends AsyncTask<String, Void, String> {

        private ProgressDialog loading;

        @Override
        protected void onPreExecute() { //thread실행전
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Checking Data..", "Please wait..", false, false);
        }

        @Override
        protected String doInBackground(String... parms) { //서버에 로그인정보(아이디,비밀번호) 전달.

            Login L = new Login();
            String msg = L.login(parms[0], parms[1]);
            return msg;
        }

        @Override
        protected void onPostExecute(String s) { //thread실행후
            super.onPostExecute(s);
            loading.dismiss();


            if (s.equals("No Matching ID"))
                textView.setText("존재하지않는 아이디입니다.");
            else if(s.equals("No Matching Password")){
                textView.setText("비밀번호가 일치하지않습니다.");
            }
            else { // 아이디, 비밀번호가 전부 일치하면 메인화면으로 전환됩니다.
                int index = s.indexOf("/");
                String key = s.substring(0,index);
                String name = s.substring(index+1, s.length());
                if(loginChecked){ // 자동로그인체크가 되어있으면 내용저장.
                    editor.putString("id", edit1.getText().toString());
                    editor.putString("pw", edit2.getText().toString());
                    editor.putString("key", key);
                    editor.putString("name", name);
                    editor.putBoolean("autoLogin", loginChecked);
                    editor.commit();
                }

                String info = prefs.getString("id","")+" "+prefs.getString("pw","")+" "+prefs.getString("key","")+" "+prefs.getString("name","");
                Log.v("TEST"+context, info);

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(context, "환영합니다.", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

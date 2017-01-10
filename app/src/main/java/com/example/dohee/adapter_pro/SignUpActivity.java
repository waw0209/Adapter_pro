package com.example.dohee.adapter_pro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextName, editTextId;
    EditText editTextPw1, editTextPw2, editTextPhone, editTextKey;
    TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextName = (EditText)findViewById(R.id.edit1);
        editTextId = (EditText)findViewById(R.id.edit2);
        editTextPw1 = (EditText)findViewById(R.id.edit3);
        editTextPw2 = (EditText)findViewById(R.id.edit4);
        editTextPhone = (EditText)findViewById(R.id.edit5);
        editTextKey = (EditText)findViewById(R.id.edit6);
        textView = (TextView)findViewById(R.id.view);

    }

    public void insert(View view){
        String name = editTextName.getText().toString();
        String id = editTextId.getText().toString();
        String pw1 = editTextPw1.getText().toString();
        String pw2 = editTextPw2.getText().toString();
        String phone = editTextPhone.getText().toString();
        String key = editTextKey.getText().toString();

        if(name.equals(""))
            textView.setText("이름을 입력해주세요.");
        else if(id.equals(""))
            textView.setText("아이디를 입력해주세요.");
        else if(pw1.equals(""))
            textView.setText("비밀번호를 입력해주세요.");
        else if(pw2.equals(""))
            textView.setText("비밀번호확인을 입력해주세요.");
        else if(phone.equals(""))
            textView.setText("연락처를 입력해주세요.");
        else if(!pw1.equals(pw2))
            textView.setText("비밀번호를 다시 입력해주세요.");
        else { // 모든정보가 다입력되고, 비밀번호1,2가 일치할때만 서버로 회원가입정보를 전송합니다.
            SignUpInfo s = new SignUpInfo();
            s.execute(key, name, id, pw1, phone);
        }
    }

    class SignUpInfo extends AsyncTask<String, Void, String> { // AsyncTask<Param, Progress, Result>

        Context context = SignUpActivity.this;
        ProgressDialog loading;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Uploading Data..", "Please wait..", false, false);
        }

        @Override
        protected String doInBackground(String... parms) { // 서버로 전송.

            SignUp S = new SignUp();
            String msg = S.signup(parms[0], parms[1], parms[2], parms[3], parms[4]);
            return msg;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if(s.equals("exist"))
                textView.setText("이미 존재하는 아이디입니다.");
            else if(s.equals("insert")) { // 정상적으로 가입되면 로그인화면으로 다시 넘어갑니다.
                Toast.makeText(context, "가입되셨습니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}

package com.example.dohee.adapter_pro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity {

    private Context context = ContactActivity.this;
    private static final int PICK_CONTACT_REQUEST = 3; // 연락처요청

    SharedPreferences prefs;
    Button button;

    ArrayList<ListItem> listItem;
    ListItemAdapter1 adapter;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        adapter = new ListItemAdapter1(this);
        listItem = new ArrayList<ListItem>();

        prefs = getSharedPreferences("MyInfo", MODE_PRIVATE);
        String key = prefs.getString("key","");


        list = (ListView)findViewById(R.id.listView);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(onClickListener);

        DownloadToServer ds = new DownloadToServer();
        ds.execute(key);
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ListItemAdapter1(this);
        listItem = new ArrayList<ListItem>();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, PICK_CONTACT_REQUEST);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null){
            if(requestCode == PICK_CONTACT_REQUEST){ // 연락처접근.

                String key = prefs.getString("key","");
                String phone = null;
                String name = null;
                Uri uri = data.getData();
                Cursor c = getContentResolver().query(uri, null, null, null, null);
                if(c.moveToFirst()) {
                    phone = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    name = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
                phone = phone.replace("-","");
                UploadToServer us = new UploadToServer();
                us.execute(key, name, phone);
            }
        }
    }

    /* 가져온 연락처정보를 서버에 전송 */
    class UploadToServer extends AsyncTask<String, Void, String> {

        ProgressDialog loading = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Checking Data..", "Please wait..", false, false);
        }

        @Override
        protected String doInBackground(String... parms) {

            Upload U = new Upload();
            String msg = U.uploadContact(parms[0], parms[1], parms[2]);
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            loading.dismiss();

            onResume();
            String key = prefs.getString("key","");
            DownloadToServer ds = new DownloadToServer();
            ds.execute(key);
        }
    }

    /* 서버에 저장된 연락처들 다운로드 */
    class DownloadToServer extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parms) {

            Download D = new Download();
            String msg = D.downloadContact(parms[0]);
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String name;
            String number;

            try {
                JSONObject root = new JSONObject(s);
                JSONArray ja = root.getJSONArray("results");
                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    name = jo.getString("name");
                    number = jo.getString("phone");
                    listItem.add(new ListItem(name, number));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i = 0 ; i<listItem.size();i++) { // 리스트에 내용저장.
                adapter.addItem(new TextItem(listItem.get(i).getData(0), listItem.get(i).getData(1)));
                list.setAdapter(adapter);
            }
        }
    }
}

package com.example.dohee.adapter_pro;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_GALLERY_REQUEST = 1; // 갤러리요청

    SharedPreferences prefs;
    private Context context = MainActivity.this;
    private Dialog mCustomDialog;
    private ImageButton gallery, contact, calendar, telephone;
    Uri fileUri;

    List<String> point_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("MyInfo", MODE_PRIVATE);

        FirebaseMessaging.getInstance().subscribeToTopic("news");
        FirebaseInstanceId.getInstance().getToken();

        mCustomDialog = new CustomDialog(this, leftListener, rightListener);

        gallery = (ImageButton) findViewById(R.id.gallery);
        contact = (ImageButton) findViewById(R.id.contact);
        calendar = (ImageButton) findViewById(R.id.calendar);
        telephone = (ImageButton) findViewById(R.id.telephone);

        gallery.setOnClickListener(buttonListener);
        contact.setOnClickListener(buttonListener);
        calendar.setOnClickListener(buttonListener);
        telephone.setOnClickListener(buttonListener);

        point_string = new ArrayList<String>(); // 일정이 있는 날짜들을 가져옴.


        if (shouldAskPermissions()) {
            askPermissions();
        }

        Sensor s = new Sensor();
        s.execute();
    }

    /* 다이얼로그 왼쪽버튼(서버에 미디어경로, 이름, 메세지전달) */
    private View.OnClickListener leftListener = new View.OnClickListener() {
        public void onClick(View view) {


            EditText edit1 = (EditText) mCustomDialog.findViewById(R.id.name);
            EditText edit2 = (EditText) mCustomDialog.findViewById(R.id.message);

            Media media = new Media(context);
            String path = media.getPath(fileUri);
            String name = edit1.getText().toString();
            String type = media.getType(path);
            String sender = prefs.getString("name", "");
            String key = prefs.getString("key", "");
            String msg = edit2.getText().toString();

            UploadToServer us = new UploadToServer();
            us.execute(path, name, type, sender, key, msg);
            mCustomDialog.dismiss();

        }
    };

    /* 다이얼로그 오른쪽버튼(다이얼로그 종료) */
    private View.OnClickListener rightListener = new View.OnClickListener() {
        public void onClick(View view) {

            mCustomDialog.dismiss();
        }
    };

    /* 버튼리스너 */
    private View.OnClickListener buttonListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            Intent intent;
            switch (view.getId()) {
                case R.id.gallery: // 미디어
                    intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
                    startActivityForResult(intent, PICK_GALLERY_REQUEST);
                    break;
                case R.id.contact: // 연락처
                    intent = new Intent(context, ContactActivity.class);
                    startActivity(intent);
                    break;
                case R.id.calendar: // 달력, 서버에서 일정이 있는 날을 가져옴.
                    CalendarPoint cp = new CalendarPoint();
                    cp.execute(prefs.getString("key", ""));
                    break;
                case R.id.telephone: // 영상통화
                    intent = new Intent(context, StreamActivity.class);
                    startActivity(intent);
                    break;
            }

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;
        fileUri = null;
        ImageView imageView = null;
        VideoView videoView = null;
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_GALLERY_REQUEST) {
                fileUri = data.getData();
                mCustomDialog.show();
                try {
                    imageView = (ImageView) mCustomDialog.findViewById(R.id.imageView);
                    videoView = (VideoView) mCustomDialog.findViewById(R.id.videoView);
                    bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), fileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Media media = new Media(context);
                if (media.getType(media.getPath(fileUri)).equals("image")) {
                    imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.GONE);
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(fileUri);
                    videoView.start();
                }
            }
        }
    }

    /* 서버에 미디어정보를 전송 */
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
            String msg = U.uploadFile(parms[0], parms[1], parms[2], parms[3], parms[4], parms[5]);
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }
    }

    /* 서버에서 일정이 있는 날짜를 가져옴 */
    class CalendarPoint extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parms) {

            Download D = new Download();
            String msg = D.downloadCalendar(parms[0]);

            String info = parms[0];
            Log.v("TEST"+context+"서버에서 일정이 있는 날짜를 가져옴", info);

            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String date;
            try {
                JSONObject root = new JSONObject(s);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    date = jo.getString("date");
                    point_string.add(date);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            } // json->string으로 변환

            CalendarAdapter.schedule_string = point_string;
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class); // 달력화면으로 변환.
            startActivity(intent);

        }
    }

    /* 서버에서 최근 sensor값을 가져오는 thread */
    class Sensor extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parms) {

            Download D = new Download();
            String msg = D.downloadSensor();
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("TEST센서", s);

            String motion = "";
            try {
                JSONObject root = new JSONObject(s);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    motion = jo.getString("motion");
                }

                if (motion.equals("0")) { // 그동안 거울을 본적이없으면 push알람을 줌.
                    Push p = new Push();
                    p.execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    /* push알람을 주는 thread */
    class Push extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parms) {

            String UPLOAD_URL = "http://222.112.247.133:80/fcm/push_notification.php";
            try {

                URL url = new URL(UPLOAD_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null)
                {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            }
            catch(Exception e){
                return null;
            }
        }
    }

    /* Permission 추가 */
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
}


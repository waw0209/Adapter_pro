package com.example.dohee.adapter_pro;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by dohee on 16. 12. 1.
 */

public class Login{

    private static final String UPLOAD_URL = "http://222.112.247.133:80/login.php";

    /* 서버에 아이디랑 비밀번호 전송 */
    public String login(String id, String pw){

        String data;

        try {
            data = "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode((String)id, "UTF-8");
            data += "&" + URLEncoder.encode("user_passwd", "UTF-8") + "=" + URLEncoder.encode((String)pw, "UTF-8");

            URL url = new URL(UPLOAD_URL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

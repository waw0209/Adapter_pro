package com.example.dohee.adapter_pro;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by dohee on 16. 12. 1.
 */

public class SignUp {

    public static final String UPLOAD_URL = "http://222.112.247.133:80/upload_data.php";

    /* 서버에 회원가입정보 저장.*/
    public String signup(String key, String name, String id, String pw, String phone){

        String data;
        try{

            data = "&" + URLEncoder.encode("mirror_key", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
            data += "&" + URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
            data += "&" + URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("user_passwd", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");
            data += "&" + URLEncoder.encode("user_phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");


            URL url = new URL(UPLOAD_URL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

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

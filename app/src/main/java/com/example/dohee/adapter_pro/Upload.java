package com.example.dohee.adapter_pro;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

/**
 * Created by dohee on 16. 12. 1.
 */

public class Upload { // http를 이용해서 서버에 전송

    int serverResponseCode = 0;

    /* mutipart를 이용해서 미디어file을 업로드 */
    public String uploadFile(String path, String name, String type, String sender, String key ,String msg){


        String UPLOAD_URL = "http://222.112.247.133:80/upload_media.php";
        HttpURLConnection conn = null;

        DataOutputStream dos;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(path);

        if (!sourceFile.isFile()){

            return path+" File not exist";

        }else {
            try {

                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_URL);


                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);//Allow Inputs
                conn.setDoOutput(true);//Allow Outputs
                conn.setUseCaches(false);//Don't use a cached Copy
                conn.setChunkedStreamingMode(1024); // 하...안되면 이걸 지워봅니다.
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("Accept-Charset","UTF-8");
                conn.setRequestProperty("Charset","UTF-8");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);


                //creating new dataoutputstream
                dos = new DataOutputStream(conn.getOutputStream());

                //writing bytes to data outputstream
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_key\"\r\n\r\n" + key);
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_sender\"\r\n\r\n" + sender);
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_msg\"\r\n\r\n" + msg);
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_name\"\r\n\r\n" + name);
                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_type\"\r\n\r\n" + type);

                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);


                dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + sourceFile + "\"" + lineEnd);
                dos.writeBytes(lineEnd);


                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();


                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();


            } catch (Exception e){
                e.printStackTrace();
            }

            if (serverResponseCode == 200) {
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    rd.close();
                } catch (Exception e) {

                }
                return sb.toString();
            }else {
                return null;
            }
        }
    }

    /* 서버에 연락처 업로드 */
    public String uploadContact(String key, String name, String phone){

        String UPLOAD_URL = "http://222.112.247.133:80/upload_phone.php";

        String data;
        try{

            data = "&" + URLEncoder.encode("mirror_key", "UTF-8") + "=" + URLEncoder.encode((String)key, "UTF-8");
            data += "&" + URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode((String)name, "UTF-8");
            data += "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode((String)phone, "UTF-8");

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

    /* 새로운 스케줄을 업로드 */
    public String uploadSchedule(String date, String date_ym, String time, String task, String key, String sender){

        String UPLOAD_URL = "http://222.112.247.133:80/upload_schedule.php";
        String data;
        try{

            data = "&" + URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8");
            data += "&" + URLEncoder.encode("date_ym", "UTF-8") + "=" + URLEncoder.encode(date_ym, "UTF-8");
            data += "&" + URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
            data += "&" + URLEncoder.encode("task", "UTF-8") + "=" + URLEncoder.encode(task, "UTF-8");
            data += "&" + URLEncoder.encode("mirror_key", "UTF-8") + "=" + URLEncoder.encode(key, "UTF-8");
            data += "&" + URLEncoder.encode("sender", "UTF-8") + "=" + URLEncoder.encode(sender, "UTF-8");


            URL url = new URL(UPLOAD_URL);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();


            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

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

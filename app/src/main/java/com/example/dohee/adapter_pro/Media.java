package com.example.dohee.adapter_pro;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

/**
 * Created by dohee on 16. 12. 1.
 */

public class Media{

    private Context context;

    Media(Context context){

        this.context = context;

    }

    /* 선택한 미디어의 위치을 알아내는 함수 */
    public String getPath(Uri uri) {

        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor c = context.getContentResolver().query(uri, proj, null, null, null);
        if (c.moveToFirst()) {
            int index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = c.getString(index);
        }
        c.close();
        return res;

    }

    /* 선택한 미디어의 형식을 알아내는 함수 */
    public String getType(String url){
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        int index = type.indexOf('/');
        type = type.substring(0,index);
        return type;

    }

}

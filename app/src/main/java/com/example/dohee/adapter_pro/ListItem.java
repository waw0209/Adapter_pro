package com.example.dohee.adapter_pro;

/**
 * Created by dohee on 16. 12. 5.
 */

public class ListItem {

    private String[] mData;

    /* 연락처 리스트에서 사용 */
    public ListItem(String name, String number){

        mData = new String[2];
        mData[0] = name;
        mData[1] = number;

    }

    /* 일정 리스트에서 사용 */
    public ListItem(String date, String time, String task, String name){

        mData = new String[4];
        mData[0] = date;
        mData[1] = time;
        mData[2] = task;
        mData[3] = name;

    }

    public String[] getData(){
        return mData;
    }

    public String getData(int index){
        return mData[index];
    }

    public void setData(String[] data){
        mData = data;
    }


}

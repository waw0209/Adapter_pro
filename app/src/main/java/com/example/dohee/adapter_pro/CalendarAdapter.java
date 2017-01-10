package com.example.dohee.adapter_pro;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class CalendarAdapter extends BaseAdapter{

    private Context context;
    private java.util.Calendar month;
    public GregorianCalendar pmonth;

    public GregorianCalendar pmonthmaxset;
    private GregorianCalendar selectedDate;
    int firstDayPos;
    int LastDay; // 달의 마지막날(30일, 31일)
    int maxWeeknumber;
    int calMaxP;
    int mnthlength;
    String itemvalue, curentDateString;
    DateFormat df;

    ImageView iw;

    private ArrayList<String> items;
    public static List<String> day_string; // 해당 달에 디스플레이되는 날짜들.
    public static List<String> schedule_string; // 스케줄이 있는 날짜들.
    private View previousView; // 이전에 선택한 날짜

    public CalendarAdapter(Context context, GregorianCalendar monthCalendar) {

        CalendarAdapter.day_string = new ArrayList<String>();
        Locale.setDefault(Locale.KOREA);
        month = monthCalendar; // 해당
        selectedDate = (GregorianCalendar) monthCalendar.clone(); // 선택한 날짜.
        this.context = context;
        month.set(GregorianCalendar.DAY_OF_MONTH, 1);

        this.items = new ArrayList<String>();
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        curentDateString = df.format(selectedDate.getTime());
        refreshDays();

    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() { return day_string.size(); }

    public Object getItem(int position) { return day_string.get(position); }

    public long getItemId(int position) { return 0; }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        TextView dayView;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.cal_item, null);

        }

        dayView = (TextView) v.findViewById(R.id.date);
        String[] separatedTime = day_string.get(position).split("-");// 날짜를 -단위로 나눠준다.

        String gridvalue = separatedTime[2].replaceFirst("^0*", "");
        if ((Integer.parseInt(gridvalue) > 1) && (position < firstDayPos)) {
            dayView.setTextColor(Color.GRAY);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else if ((Integer.parseInt(gridvalue) < 7) && (position > 28)) {
            dayView.setTextColor(Color.GRAY);
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            dayView.setTextColor(Color.WHITE);
        }


        if (day_string.get(position).equals(curentDateString)) { // 오늘과 날짜가 같으면 배경색을 칠해준다.
            v.setBackgroundColor(Color.parseColor("#FAE0D4"));
            dayView.setTextColor(Color.parseColor("#343434"));
        } else {
            v.setBackgroundColor(Color.parseColor("#343434"));
            dayView.setTextColor(Color.WHITE);
        }


        dayView.setText(gridvalue);

        // create date string for comparison
        String date = day_string.get(position);

        if (date.length() == 1) {
            date = "0" + date;

        }
        String monthStr = "" + (month.get(GregorianCalendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        iw = (ImageView) v.findViewById(R.id.date_icon);
        iw.setImageResource(R.drawable.hearts);
        iw.setVisibility(View.GONE);

        setEventView(v, position);

        return v;
    }

    public View setSelected(View view,int pos) {


        TextView d = (TextView)view.findViewById(R.id.date);

        if (previousView != null) {
            TextView pre_d = (TextView)previousView.findViewById(R.id.date);
            previousView.setBackgroundColor(Color.parseColor("#343434"));
            pre_d.setTextColor(Color.WHITE);

        }

        view.setBackgroundColor(Color.parseColor("#FAE0D4"));// 선택한 아이템의 배경을 바꿔준다.
        d.setTextColor(Color.parseColor("#343434"));

        int len = day_string.size();

        if(len > pos)
            if(!day_string.get(pos).equals(curentDateString))
                previousView = view;

        return view;
    }

    public void refreshDays() {
        items.clear();
        day_string.clear();
        Locale.setDefault(Locale.KOREA);
        pmonth = (GregorianCalendar) month.clone();
        firstDayPos = month.get(GregorianCalendar.DAY_OF_WEEK);
        maxWeeknumber = month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
        mnthlength = maxWeeknumber * 7;
        LastDay = getLastDay();
        calMaxP = LastDay - (firstDayPos - 1);
        pmonthmaxset = (GregorianCalendar) pmonth.clone();
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        for (int n = 0; n < mnthlength; n++) {

            itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            day_string.add(itemvalue);
        }
    }

    private int getLastDay() {
        int maxDay;
        Boolean check = (month.get(GregorianCalendar.MONTH)+1) == 1;
        if (check) {
            pmonth.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            pmonth.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
        }
        maxDay = pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxDay;
    }

    public void setEventView(View v, int pos){

        int len = schedule_string.size();
        String d = day_string.get(pos).replace("-",".");
        //Log.v("TEST pos "+pos, d+" "+schedule_string);
        for(int i=0;i<len;i++){
            if(schedule_string.get(i).equals(d)){
                iw.setVisibility(View.VISIBLE);
                //Log.v("TEST----------3", schedule_string.get(i));
            }
        }
    }
}

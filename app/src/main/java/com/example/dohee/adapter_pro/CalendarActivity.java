package com.example.dohee.adapter_pro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class CalendarActivity extends AppCompatActivity {

    Context context = CalendarActivity.this;

    SharedPreferences prefs;
    public GregorianCalendar cal, cal_copy;
    private CalendarAdapter cal_adapter;
    private TextView tv_month;

    ArrayList<ListItem> listItem;
    ListItemAdapter2 adapter;
    ListView list;
    RelativeLayout layout;

    TextToSpeech tts;
    String ttsSpeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        prefs = getSharedPreferences("MyInfo", MODE_PRIVATE);

        tts = new TextToSpeech (this, new TextToSpeech.OnInitListener () {
            public void onInit (int status) {
                tts.setLanguage (java.util.Locale.KOREAN);
            }
        });

        ttsSpeak="";

        adapter = new ListItemAdapter2(this);
        listItem = new ArrayList<ListItem>();
        list = (ListView) findViewById(R.id.listView);

        layout = (RelativeLayout)findViewById(R.id.r_layout);


        cal = (GregorianCalendar) GregorianCalendar.getInstance();
        cal_copy = (GregorianCalendar) cal.clone();
        cal_adapter = new CalendarAdapter(this, cal);


        tv_month = (TextView) findViewById(R.id.tv_month);
        tv_month.setText(DateFormat.format("yyyy년 MM월", cal));

        ImageButton previous = (ImageButton) findViewById(R.id.ib_prev);
        ImageButton next = (ImageButton) findViewById(R.id.ib_next);


        previous.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                setPreviousMonth();
                refreshCalendar();
                list.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setNextMonth();
                refreshCalendar();
                list.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
            }
        });

        GridView gridview = (GridView) findViewById(R.id.gv_calendar);
        gridview.setAdapter(cal_adapter);


        // 짧게 클릭하면 아래쪽에 당일일정출력
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long l) {

                ((CalendarAdapter) parent.getAdapter()).setSelected(v, position);
                String selectedGridDate = CalendarAdapter.day_string.get(position); // 클릭한 날짜.

                String[] separatedTime = selectedGridDate.split("-"); // '.'를 기준으로 나눈다.
                String gridvalueString = separatedTime[2].replaceFirst("^0*", "");
                int gridvalue = Integer.parseInt(gridvalueString); // 실제날짜(ex> 1일이면 1)
                // pos: gridTable에서 몇번째인지.

                if ((gridvalue > 10) && (position < 8)) {
                    setPreviousMonth();
                    refreshCalendar();
                } else if ((gridvalue < 7) && (position > 28)) {
                    setNextMonth();
                    refreshCalendar();
                }
                ((CalendarAdapter) parent.getAdapter()).setSelected(v, position);

                onResume();

                selectedGridDate = selectedGridDate.replace("-", ".");
                String key = prefs.getString("key", "");

                ScheduleDown sd = new ScheduleDown();
                sd.execute(selectedGridDate, key);

            }

        });

        // 길게 클릭하면 일정입력으로 이동
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long l) {

                ((CalendarAdapter) parent.getAdapter()).setSelected(v, position);
                String selectedGridDate = CalendarAdapter.day_string.get(position); // 클릭한 날짜.

                Bundle bundle = new Bundle();
                bundle.putString("Date", selectedGridDate);

                Intent intent = new Intent(CalendarActivity.this, ScheduleActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, Activity.RESULT_FIRST_USER);

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        adapter = new ListItemAdapter2(this);
        listItem = new ArrayList<ListItem>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    protected void setNextMonth() { // 다음달로 이동

        Boolean check = (cal.get(Calendar.MONTH) + 1) == 12;// 12월이고, 이전으로 가려고 하면 연도를 1년 늘려줌.
        if (check) {
            cal.set((cal.get(GregorianCalendar.YEAR) + 1), cal.getActualMinimum(GregorianCalendar.MONTH), 1);
        } else {
            cal.set(GregorianCalendar.MONTH, cal.get(GregorianCalendar.MONTH) + 1);
        }

    }

    protected void setPreviousMonth() { // 이전달로 이동

        Boolean check = (cal.get(Calendar.MONTH) + 1) == 1; // 1월이고, 이전으로 가려고 하면 연도를 1년 줄여줌.
        if (check) {
            cal.set((cal.get(GregorianCalendar.YEAR) - 1), cal.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            cal.set(GregorianCalendar.MONTH, cal.get(GregorianCalendar.MONTH) - 1);
        }

    }

    public void refreshCalendar() {
        cal_adapter.refreshDays();
        cal_adapter.notifyDataSetChanged();
        tv_month.setText(DateFormat.format("yyyy년 MM월", cal));

    }

    class ScheduleDown extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... parms) {

            Download D = new Download();
            String msg = D.downloadSchedule(parms[0], parms[1]);
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.v("TEST목록다운로드", s);

            String date, time, task, name;
            listItem = new ArrayList<ListItem>();
            try {
                JSONObject root = new JSONObject(s);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    date = jo.getString("date");
                    time = jo.getString("time");
                    task = jo.getString("task");
                    name = jo.getString("sender");
                    listItem.add(new ListItem(date, time, task, name));

                    String[] t = time.split(":");
                    String h = t[0];
                    String m = t[1];
                    ttsSpeak += name+"님께서 "+h+"시 "+m+"분 "+task+" 일정이있습니다.\n";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (listItem.size() == 0) {
                list.setVisibility(View.GONE);
                layout.setVisibility(View.GONE);
            } else {
                list.setVisibility(View.VISIBLE);
                layout.setVisibility(View.VISIBLE);
                for (int i = 0; i < listItem.size(); i++) {
                    String s0 = listItem.get(i).getData(0);
                    String s1 = listItem.get(i).getData(1);
                    String s2 = listItem.get(i).getData(2);
                    String s3 = listItem.get(i).getData(3);

                    adapter.addItem(new TextItem(s0, s1, s2, s3));
                    list.setAdapter(adapter);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(ttsSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
            }
            else{
                tts.speak(ttsSpeak,TextToSpeech.QUEUE_FLUSH, null);
            }
            Log.v("TEST- ttsSpeak", ttsSpeak);
            ttsSpeak = "";

        }
    }

}

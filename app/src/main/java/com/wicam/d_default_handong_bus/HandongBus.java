package com.wicam.d_default_handong_bus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.wicam.R;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.WicamColors;

import android.R.integer;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hyeonmin on 2015-07-15.
 */
public class HandongBus extends Activity {

    private ProgressDialog progressDialog;
    SchedulePhp schedule_weekday_php, schedule_weekend_php;
    private ListView weekdayListView, weekendListView;
    private LinearLayout heunghaeSchedule;
    private ArrayList<BusYgrData> weekdayList = new ArrayList<BusYgrData>();
    private ArrayList<BusYgrData> weekendList = new ArrayList<BusYgrData>();
    private BusYgrAdapter weekdayAdapter, weekendAdapter;
    private Date today;
    private Calendar calendar;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.KOREA);

    private Boolean was_divider = false;
    private boolean past = false;
    private boolean weekday_check = false, weekend_check = false;
    private String format;
    private String phone_no;
    private Intent dial_intent;

    private LinearLayout ygrTopbar, heunghaeTopbar, bottomBar;

    private Button weekdayBtn, weekendBtn, heunghaeBtn, taxiBtn, outBtn, allBtn, inBtn, time1, time2, time4, time5, mapBtn;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //
        setContentView(R.layout.d_handong_bus);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("버스시간표 다운로드 중");

        Date today = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(today);
        format = formatter.format(today);

        Singleton.create().setOut_all_in(1);

        weekdayListView = (ListView)findViewById(R.id.handong_bus_weekday);
        weekendListView = (ListView)findViewById(R.id.handong_bus_weekend);
        heunghaeSchedule = (LinearLayout)findViewById(R.id.handong_bus_henghae);

        weekdayBtn = (Button)findViewById(R.id.weekday_btn);
        weekendBtn = (Button)findViewById(R.id.weekend_btn);

        ygrTopbar = (LinearLayout)findViewById(R.id.ygr_topBar);
        heunghaeTopbar = (LinearLayout)findViewById(R.id.heunghae_topBar);
        bottomBar = (LinearLayout)findViewById(R.id.handong_bus_bottomBar);

        weekdayBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        weekdayBtn.setBackgroundResource(R.drawable.rounded_left_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        weekendBtn.setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        heunghaeBtn.setBackgroundResource(R.drawable.rounded_right_half_blue);
                        weekdayListView.setVisibility(View.VISIBLE);
                        weekendListView.setVisibility(View.GONE);
                        heunghaeSchedule.setVisibility(View.GONE);
                        ygrTopbar.setVisibility(View.VISIBLE);
                        heunghaeTopbar.setVisibility(View.GONE);
                        bottomBar.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });
        weekendBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        weekendBtn.setBackgroundColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        weekdayBtn.setBackgroundResource(R.drawable.rounded_left_half_blue);
                        heunghaeBtn.setBackgroundResource(R.drawable.rounded_right_half_blue);
                        weekdayListView.setVisibility(View.GONE);
                        weekendListView.setVisibility(View.VISIBLE);
                        heunghaeSchedule.setVisibility(View.GONE);
                        ygrTopbar.setVisibility(View.VISIBLE);
                        heunghaeTopbar.setVisibility(View.GONE);
                        bottomBar.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });

        heunghaeBtn = (Button)findViewById(R.id.heunghae_btn);
        heunghaeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        heunghaeBtn.setBackgroundResource(R.drawable.rounded_right_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        weekdayBtn.setBackgroundResource(R.drawable.rounded_left_half_blue);
                        weekendBtn.setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        weekdayListView.setVisibility(View.GONE);
                        weekendListView.setVisibility(View.GONE);
                        heunghaeSchedule.setVisibility(View.VISIBLE);
                        ygrTopbar.setVisibility(View.GONE);
                        heunghaeTopbar.setVisibility(View.VISIBLE);
                        bottomBar.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        taxiBtn = (Button)findViewById(R.id.taxi_btn);
        taxiBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        taxiBtn.setBackgroundResource(R.drawable.rounded_semi_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        taxiBtn.setBackgroundResource(R.drawable.rounded_blue);
                        new AlertDialog.Builder(HandongBus.this).setItems(R.array.taxi, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                switch (which) {
                                    case 0:
                                        dial_intent = new Intent(Intent.ACTION_DIAL);
                                        phone_no = "0542312330";
                                        dial_intent.setData(Uri.parse("tel:" + phone_no));
                                        startActivity(dial_intent);
                                        break;
                                    case 1:
                                        dial_intent = new Intent(Intent.ACTION_DIAL);
                                        phone_no = "0542826161";
                                        dial_intent.setData(Uri.parse("tel:" + phone_no));
                                        startActivity(dial_intent);
                                        break;
                                    case 2:
                                        dial_intent = new Intent(Intent.ACTION_DIAL);
                                        phone_no = "054-276-1442";
                                        dial_intent.setData(Uri.parse("tel:" + phone_no));
                                        startActivity(dial_intent);
                                        break;
                                    case 3:
                                        dial_intent = new Intent(Intent.ACTION_DIAL);
                                        phone_no = "0542521111";
                                        dial_intent.setData(Uri.parse("tel:" + phone_no));
                                        startActivity(dial_intent);
                                        break;
                                }
                            }
                        }).show();
                        break;
                }
                return false;
            }
        });

        time1 = (Button)findViewById(R.id.handong_bus_time_1);
        time2 = (Button)findViewById(R.id.handong_bus_time_2);
        time4 = (Button)findViewById(R.id.handong_bus_time_4);
        time5 = (Button)findViewById(R.id.handong_bus_time_5);

        outBtn = (Button)findViewById(R.id.handong_bus_out_btn);
        allBtn = (Button)findViewById(R.id.handong_bus_all_btn);
        inBtn = (Button)findViewById(R.id.handong_bus_in_btn);

        outBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        outBtn.setBackgroundResource(R.drawable.rounded_left_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        allBtn.setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        inBtn.setBackgroundResource(R.drawable.rounded_right_half_blue);
                        Singleton.create().setOut_all_in(0);
                        time1.setVisibility(View.VISIBLE);
                        time2.setVisibility(View.VISIBLE);
                        time4.setVisibility(View.GONE);
                        time5.setVisibility(View.GONE);
                        weekdayAdapter = new BusYgrAdapter(HandongBus.this, weekdayList);
                        weekdayListView.setAdapter(weekdayAdapter);
                        weekendAdapter = new BusYgrAdapter(HandongBus.this, weekendList);
                        weekendListView.setAdapter(weekendAdapter);

                        break;
                }
                return false;
            }
        });

        allBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        allBtn.setBackgroundColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        outBtn.setBackgroundResource(R.drawable.rounded_left_half_blue);
                        inBtn.setBackgroundResource(R.drawable.rounded_right_half_blue);
                        Singleton.create().setOut_all_in(1);
                        time1.setVisibility(View.VISIBLE);
                        time2.setVisibility(View.VISIBLE);
                        time4.setVisibility(View.VISIBLE);
                        time5.setVisibility(View.VISIBLE);
                        weekdayAdapter = new BusYgrAdapter(HandongBus.this, weekdayList);
                        weekdayListView.setAdapter(weekdayAdapter);
                        weekendAdapter = new BusYgrAdapter(HandongBus.this, weekendList);
                        weekendListView.setAdapter(weekendAdapter);
                        break;
                }
                return false;
            }
        });

        inBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        inBtn.setBackgroundResource(R.drawable.rounded_right_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        outBtn.setBackgroundResource(R.drawable.rounded_left_half_blue);
                        allBtn.setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        Singleton.create().setOut_all_in(2);
                        time1.setVisibility(View.GONE);
                        time2.setVisibility(View.GONE);
                        time4.setVisibility(View.VISIBLE);
                        time5.setVisibility(View.VISIBLE);
                        weekdayAdapter = new BusYgrAdapter(HandongBus.this, weekdayList);
                        weekdayListView.setAdapter(weekdayAdapter);
                        weekendAdapter = new BusYgrAdapter(HandongBus.this, weekendList);
                        weekendListView.setAdapter(weekendAdapter);
                        break;
                }
                return false;
            }
        });

        mapBtn = (Button)findViewById(R.id.handong_bus_map_btn);
        mapBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mapBtn.setBackgroundResource(R.drawable.rounded_semi_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        mapBtn.setBackgroundResource(R.drawable.rounded_blue);
                        startActivity(new Intent(HandongBus.this, HandongBusMap.class));
                        break;
                }
                return false;
            }
        });

        if (calendar.get(Calendar.DAY_OF_WEEK) == 7 || calendar.get(Calendar.DAY_OF_WEEK) == 1) {
            weekendListView.setVisibility(View.VISIBLE);
            weekdayListView.setVisibility(View.GONE);
            heunghaeSchedule.setVisibility(View.GONE);
            weekdayBtn.setBackgroundResource(R.drawable.rounded_left_half_blue);
            weekendBtn.setBackgroundResource(R.color.wc_blue);
        }
        else {
            weekendListView.setVisibility(View.GONE);
            weekdayListView.setVisibility(View.VISIBLE);
            heunghaeSchedule.setVisibility(View.GONE);
            weekdayBtn.setBackgroundResource(R.drawable.rounded_left_blue);
            weekendBtn.setBackgroundResource(R.color.wc_half_blue);
        }

        schedule_weekday_php = new SchedulePhp(weekdayList, this, false);
        schedule_weekday_php.execute(new Security().WEB_BUS_ADDRESS + "bus_schedule.php?day=0");
        schedule_weekend_php = new SchedulePhp(weekendList, this, true);
        schedule_weekend_php.execute(new Security().WEB_BUS_ADDRESS + "bus_schedule.php?day=1");

    }

    public class SchedulePhp extends AsyncTask<String, integer, String> {
        private ListAdapter adapter;
        private ArrayList<BusYgrData> list;
        private HandongBus context;
        private Boolean weekend;

        public SchedulePhp(ArrayList<BusYgrData> ygr_list, HandongBus context, Boolean weekend){
            super();
            this.list = ygr_list;
            this.context = context;
            this.weekend = weekend;
            context.showProgressDialog();
        }

        protected String doInBackground(String... urls) {
            StringBuilder jsonHtml = new StringBuilder();
            String return_str="";

            while (return_str.equalsIgnoreCase("")) {
                try{
                    URL data_url = new URL(urls[0]);
                    System.out.println(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection)data_url.openConnection();
                    if(conn != null){
                        conn.setConnectTimeout(10000);
                        conn.setUseCaches(false);
                        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            for(;;){
                                String line = br.readLine();
                                if(line == null) break;
                                jsonHtml.append(line + "\n");
                            }
                            br.close();
                        }
                        conn.disconnect();
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
                return_str = jsonHtml.toString();
            }

            return jsonHtml.toString();
        }
        protected void onPostExecute(String str){
            try{
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");

                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);

                    Boolean hide_line = false;

                    if (list.size() == 0 || was_divider) {
                        was_divider = false;
                        hide_line = true;
                    }

                    if (jo.getString("divider").equalsIgnoreCase("1"))
                        was_divider = true;

                    if (weekend) {
                        list.add(new BusYgrData(
                                jo.getString("col1"), 	jo.getString("col2"), jo.getString("col3"), jo.getString("col4"), jo.getString("col5"),
                                jo.getString("col123"), jo.getString("col45"), jo.getString("divider"), hide_line, past
                        ));
                        weekend_past_check();
                    }
                    else {
                        list.add(new BusYgrData(
                                jo.getString("col1"), 	jo.getString("col2"), jo.getString("col3"), jo.getString("col4"), jo.getString("col5"),
                                jo.getString("col123"), jo.getString("col45"), jo.getString("divider"), hide_line, past
                        ));
                        weekday_past_check();
                    }
                }

                //------------------------------------------------------------------------------------------------------------------------- 순서뒤섞기 시작

                if (weekend) {
                    list.add(new BusYgrData("", "", "", "", "", "", "", "", false, false)); //마지막 to 띄우기
                    weekendAdapter = new BusYgrAdapter(HandongBus.this, list);
                    weekendListView.setAdapter(weekendAdapter);
                }
                else {
                    list.add(new BusYgrData("", "", "", "", "", "", "", "", false, false)); //마지막 to 띄우기
                    weekdayAdapter = new BusYgrAdapter(HandongBus.this, list);
                    weekdayListView.setAdapter(weekdayAdapter);
                }

                context.dismissProgressDialog();

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public void weekday_past_check() {
        if (!weekday_check) {
            if (toInt(format) < toInt(weekdayList.get(weekdayList.size()-1).getS1()) ||
                    toInt(format) < toInt(weekdayList.get(weekdayList.size()-1).getS2()) ||
                    toInt(format) < toInt(weekdayList.get(weekdayList.size()-1).getS3()) ||
                    toInt(format) < toInt(weekdayList.get(weekdayList.size()-1).getS4()) ||
                    toInt(format) < toInt(weekdayList.get(weekdayList.size()-1).getS5())
                    ) {
                weekdayList.get(weekdayList.size()-1).setPast(true);

                weekday_check = true;
            }
        }
    }

    public void weekend_past_check() {
        if (!weekend_check) {
            if (toInt(format) < toInt(weekendList.get(weekendList.size()-1).getS1()) ||
                    toInt(format) < toInt(weekendList.get(weekendList.size()-1).getS2()) ||
                    toInt(format) < toInt(weekendList.get(weekendList.size()-1).getS3()) ||
                    toInt(format) < toInt(weekendList.get(weekendList.size()-1).getS4()) ||
                    toInt(format) < toInt(weekendList.get(weekendList.size()-1).getS5())
                    ) {
                weekendList.get(weekendList.size()-1).setPast(true);

                weekend_check = true;
            }
        }
    }

    public int toInt(String str) {
        if (!str.equalsIgnoreCase("") && !str.equalsIgnoreCase("-"))
            return Integer.parseInt(str.replace(":", ""));
        else
            return 0;
    }

    public void showProgressDialog() {
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

}

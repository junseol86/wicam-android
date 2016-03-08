package com.wicam.c_main_page.dashboard;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.c_main_page.MainPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-04-13.
 */
public class DashboardAsyncTask extends AsyncTask<String, integer, String> {

    private MainPage mainPage;
    private ArrayList<DashboardData> dashboardList;

    public DashboardAsyncTask(MainPage mainPage, ArrayList<DashboardData> dashboardList) {
        this.mainPage = mainPage;
        this.dashboardList = dashboardList;
        Singleton.create().setCanBindView(false);
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder jsonHtml = new StringBuilder();
        String return_str="";
        urls[0] = urls[0].replace(" ", "%20").replace("'", "%27");
//        System.out.println(urls[0]);

        while (return_str.equalsIgnoreCase("")) {
            try{
                URL data_url = new URL(urls[0]);
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

    @Override
    protected void onPostExecute(String str) {
        try{
            JSONObject root = new JSONObject(str);

            int num_results = root.getInt("num_results");
            int page = root.getInt("page");
            int unit = root.getInt("unit");

            if (num_results != 0) {
                if (num_results <= (page+1)*unit) {
                    mainPage.setDownloadedAllInServer(true);
                }

                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    dashboardList.add(new DashboardData(
                            jo.getString("idx"), jo.getString("content_idx"), jo.getString("photo_idx"), jo.getString("title"), jo.getString("text"), jo.getString("person"),
                            jo.getString("value1"), jo.getString("value2"), jo.getString("time"), jo.getString("content"), jo.getString("school"), jo.getInt("has_pic"),
                            (i == ja.length() - 1) && (num_results > ((page+1)*unit)), false));
                }

            }
            else
                dashboardList.add(new DashboardData(
                    "", "", "", "", "", "", "", "", "", "", "",
                    0,
                    false, true)); // 아무 항목도 없다면 '없습니다' 아이템 추가

            Singleton.create().setCanBindView(true);
            mainPage.showDownloadedList();
            mainPage.setDownloading(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

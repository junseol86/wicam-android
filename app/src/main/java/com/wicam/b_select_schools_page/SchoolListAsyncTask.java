package com.wicam.b_select_schools_page;

import android.R.integer;
import android.os.AsyncTask;

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
public class SchoolListAsyncTask extends AsyncTask<String, integer, String> {

    SelectSchoolActivity selectSchoolActivity;
    ArrayList<SchoolData> schoolList;

    public SchoolListAsyncTask(SelectSchoolActivity selectSchoolActivity, ArrayList<SchoolData> schoolList) {
        this.selectSchoolActivity = selectSchoolActivity;
        this. schoolList = schoolList;
    }


    @Override
    protected String doInBackground(String... urls) {
        StringBuilder jsonHtml = new StringBuilder();
        String return_str="";
        urls[0] = urls[0].replace(" ", "%20").replace("'", "%27");
        System.out.println(urls[0]);

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
            JSONArray ja = root.getJSONArray("results");

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                schoolList.add(new SchoolData(jo.getString("school_id"), jo.getString("school_name"), jo.getString("school_contents")));
            }

            selectSchoolActivity.showDownloadedList();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

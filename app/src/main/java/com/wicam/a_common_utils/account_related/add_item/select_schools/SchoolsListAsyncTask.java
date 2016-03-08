package com.wicam.a_common_utils.account_related.add_item.select_schools;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.b_select_schools_page.SchoolData;

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
public class SchoolsListAsyncTask extends AsyncTask<String, integer, String> {

    SelectSchoolsActivity selectSchoolsActivity;
    ArrayList<SchoolsIdNameData> schoolList;

    public SchoolsListAsyncTask(SelectSchoolsActivity selectSchoolsActivity) {
        this.selectSchoolsActivity = selectSchoolsActivity;
        this.schoolList = selectSchoolsActivity.getDownloadedSchoolList();
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
                selectSchoolsActivity.getDownloadedSchoolList().add(new SchoolsIdNameData(jo.getString("school_id"), jo.getString("school_name")));
            }

            selectSchoolsActivity.showDownloadedResult();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

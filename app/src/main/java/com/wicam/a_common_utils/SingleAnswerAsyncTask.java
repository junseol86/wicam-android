package com.wicam.a_common_utils;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.b_intro_page.IntroActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hyeonmin on 2015-04-13.
 */
public class SingleAnswerAsyncTask extends AsyncTask<String, integer, String> {
    // 한 번의 쿼리를 실행하기 위한 클래스.  어느 액티비티에서 실행하는가에 따라 다르게 동작한다.

    Object object;

    public SingleAnswerAsyncTask(Object object, Context context) {
        this.object = object;
    }


    @Override
    protected String doInBackground(String... urls) {
        StringBuilder jsonHtml = new StringBuilder();
        String return_str="";
        urls[0] = urls[0].replace(" ", "%20").replace("'", "%27");

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

            if (object instanceof  IntroActivity) {

                int versionCode = 0;

                try {
                    versionCode = ((Activity)object).getPackageManager().getPackageInfo("com.wicam", 0).versionCode;
                }
                catch (Exception e) {

                };

                if (versionCode < root.getInt("result"))
                    ((IntroActivity) object).getNewVersion();
                else
                    ((IntroActivity) object).goToSelectSchoolPage();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

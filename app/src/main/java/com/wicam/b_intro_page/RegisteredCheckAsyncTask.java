package com.wicam.b_intro_page;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Singleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hyeonmin on 2015-04-13.
 */
public class RegisteredCheckAsyncTask extends AsyncTask<String, integer, String> {

    IntroActivity introActivity;

    public RegisteredCheckAsyncTask(IntroActivity introActivity) {
        this.introActivity = introActivity;
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
            new MyCache(introActivity).setMyId(root.getString("user_id"));
            new MyCache(introActivity).setMyNickname(root.getString("user_nickname"));
            new MyCache(introActivity).setAuthority(root.getString("user_memo"));
            new MyCache(introActivity).setBlackList(root.getInt("blacklist"));

            if (!new MyCache(introActivity).getMyId().equalsIgnoreCase(""))
                introActivity.versionCheck();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

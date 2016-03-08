package com.wicam.c_main_page.content_drawer;

import android.R.integer;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-04-13.
 */
public class ContentFavoriteAsyncTask extends AsyncTask<String, integer, String> {

    ContentDrawer contentDrawer;
    ArrayList<ContentListData> contentList;
    int position;

    public ContentFavoriteAsyncTask(ContentDrawer contentDrawer, ArrayList<ContentListData> contentList, int position) {
        this.contentDrawer = contentDrawer;
        this.contentList = contentList;
        this.position = position;
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

        try {
            JSONObject root = new JSONObject(str);

            contentList.get(position).setFavorite(contentList.get(position).getDefault_code().equalsIgnoreCase("0") ?  root.getInt("result") : 1 - root.getInt("result"));
            contentDrawer.refreshAnItem(position);

        }
        catch (Exception e) {

        }
    }
}

package com.wicam.c_main_page.content_drawer;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.common_values.Singleton;

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
public class ContentListAsyncTask extends AsyncTask<String, integer, String> {

    ContentDrawer contentDrawer;
    ArrayList<ContentListData> contentList;

    public ContentListAsyncTask(ContentDrawer contentDrawer, ArrayList<ContentListData> contentList) {
        this.contentDrawer = contentDrawer;
        this.contentList = contentList;
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
            JSONArray ja = root.getJSONArray("results");
            int num_results = root.getInt("num_results");
            int page = root.getInt("page");
            int unit = root.getInt("unit");

            if (num_results <= (page+1)*unit) {
                contentDrawer.setDownloadedAllInServer(true);
            }

            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);

                ArrayList<String> schoolIds = new ArrayList<String>();
                JSONArray schoolIdArray = jo.getJSONArray("school_id_list");
                for (int j = 0; j < schoolIdArray.length(); j++)
                    schoolIds.add(schoolIdArray.getString(j));

                ArrayList<String> schoolNames = new ArrayList<String>();
                JSONArray schoolNameArray = jo.getJSONArray("school_name_list");
                for (int j = 0; j < schoolNameArray.length(); j++)
                    schoolNames.add(schoolNameArray.getString(j));

                contentList.add(new ContentListData(schoolIds, schoolNames,
                                jo.getString("content_id"), jo.getString("default_code"), jo.getString("content_name"), jo.getString("content_type"), jo.getString("description"),
                                jo.getString("url_link"), jo.getString("package_name"), jo.getString("contact"), jo.getString("writer_id"), jo.getString("modifier_nickname"),
                                jo.getString("modify_time"), jo.getInt("favorite"), (i == ja.length() - 1) && (num_results > ((page+1)*unit))));
            }

            Singleton.create().setCanBindView(true);
            contentDrawer.contentListDownResult();
            contentDrawer.setDownloading(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

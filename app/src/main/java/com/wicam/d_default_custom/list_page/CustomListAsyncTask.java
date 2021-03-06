package com.wicam.d_default_custom.list_page;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_custom.CustomData;
import com.wicam.d_default_custom.list_page.CustomListActivity;

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
public class CustomListAsyncTask extends AsyncTask<String, integer, String> {

    CustomListActivity customListActivity;
    ArrayList<ItemData> customList;

    public CustomListAsyncTask(CustomListActivity customListActivity, ArrayList<ItemData> customList) {
        this.customListActivity = customListActivity;
        this.customList = customList;
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
                    customListActivity.setDownloadedAllInServer(true);
                }

                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);


                    customList.add(new CustomData(
                            jo.getString("custom_id"), jo.getString("custom_name"), jo.getString("url_link"), jo.getString("description"),
                            jo.getString("phone1"), jo.getString("phone2"),
                            jo.getString("writer_id"), jo.getString("modifier_id"), jo.getString("modifier_nickname"), jo.getString("modify_time"),
                            jo.getInt("has_photo"),
                            jo.getInt("comments"), jo.getInt("reports"),
                            jo.getInt("my_favorite"), jo.getInt("likes"), jo.getInt("my_like"), jo.getInt("authority"),
                            (i == ja.length() - 1) && (num_results > ((page+1)*unit)), false));
                }
            }
            else
                customList.add(new CustomData(
                                                            "", "", "", "", "", "", "", "", "", "",
                                                            0,
                                                            0, 0, 0, 0, 0, 0,
                                                            false, true)); // 아무 항목도 없다면 '없습니다' 아이템 추가

            Singleton.create().setCanBindView(true);
            customListActivity.showDownloadedList();
            customListActivity.setDownloading(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

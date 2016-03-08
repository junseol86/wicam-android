package com.wicam.d_default_lecture;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
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
public class LectureListAsyncTask extends AsyncTask<String, integer, String> {

    LectureListActivity lectureListActivity;
    ArrayList<ItemData> lectureList;

    public LectureListAsyncTask(LectureListActivity lectureListActivity, ArrayList<ItemData> lectureList) {
        this.lectureListActivity = lectureListActivity;
        this.lectureList = lectureList;
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
                    lectureListActivity.setDownloadedAllInServer(true);
                }

                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);

                    lectureList.add(new LectureData(
                            jo.getString("lecture_id"), jo.getString("lecture_name"), jo.getString("professor_name"), jo.getString("major"),
                            jo.getDouble("avg_difficulty"), jo.getDouble("avg_instructiveness"),
                            jo.getInt("my_difficulty"), jo.getInt("my_instructiveness"), jo.getString("my_description"),
                            jo.getString("writer_id"), jo.getString("write_time"),
                            jo.getInt("authority"),
                            (i == ja.length() - 1) && (num_results > ((page+1)*unit)), false));
                }
            }
            else
                lectureList.add(new LectureData("", "", "", "", 0.0, 0.0, 0, 0, "", "", "", 0, false, true)); // 아무 항목도 없다면 '없습니다' 아이템 추가

            Singleton.create().setCanBindView(true);
            lectureListActivity.showDownloadedList();
            lectureListActivity.setDownloading(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

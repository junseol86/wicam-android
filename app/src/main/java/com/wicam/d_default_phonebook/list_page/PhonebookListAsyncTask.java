package com.wicam.d_default_phonebook.list_page;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_phonebook.PhonebookData;

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
public class PhonebookListAsyncTask extends AsyncTask<String, integer, String> {

    PhonebookListActivity phonebookListActivity;
    ArrayList<ItemData> phonebookList;

    public PhonebookListAsyncTask(PhonebookListActivity phonebookListActivity, ArrayList<ItemData> phonebookList) {
        this.phonebookListActivity = phonebookListActivity;
        this.phonebookList = phonebookList;
        Singleton.create().setCanBindView(false);
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

            int num_results = root.getInt("num_results");
            int page = root.getInt("page");
            int unit = root.getInt("unit");


            if (num_results != 0) {
                if (num_results <= (page+1)*unit) {
                    phonebookListActivity.setDownloadedAllInServer(true);
                }

                JSONArray ja = root.getJSONArray("results");
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

                    phonebookList.add(new PhonebookData(schoolIds, schoolNames,
                            jo.getString("phonebook_id"), jo.getString("phonebook_name"), jo.getString("kind"), jo.getString("url_link"), jo.getString("description"),
                            jo.getString("phone1"), jo.getString("phone2"),
                            jo.getString("writer_id"), jo.getString("modifier_id"), jo.getString("modifier_nickname"), jo.getString("modify_time"),
                            jo.getInt("has_photo"),
                            jo.getInt("comments"), jo.getInt("reports"),
                            jo.getInt("my_favorite"), jo.getInt("likes"), jo.getInt("my_like"), jo.getInt("authority"),
                            (i == ja.length() - 1) && (num_results > ((page+1)*unit)), false));
                }
            }
            else
                phonebookList.add(new PhonebookData(new ArrayList<String>(), new ArrayList<String>(),
                                                            "", "", "", "", "", "", "", "", "", "", "",
                                                            0,
                                                            0, 0, 0, 0, 0, 0,
                                                            false, true)); // 아무 항목도 없다면 '없습니다' 아이템 추가

            Singleton.create().setCanBindView(true);
            phonebookListActivity.showDownloadedList();
            phonebookListActivity.setDownloading(false);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

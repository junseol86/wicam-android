package com.wicam.a_common_utils;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentData;
import com.wicam.a_common_utils.common_values.Singleton;

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
public class CommentLikeAsyncTask extends AsyncTask<String, integer, String> {

    DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    ArrayList<ItemAndCommentData> itemAndCommentDataList;
    int position;

    public CommentLikeAsyncTask(DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, ArrayList<ItemAndCommentData> itemAndCommentDataList, int position) {
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.itemAndCommentDataList = itemAndCommentDataList;
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
        try{
            JSONObject root = new JSONObject(str);

            Singleton.create().setCanBindView(true);

            ((CommentData)itemAndCommentDataList.get(position)).setLikes(root.getInt("likes"));
            ((CommentData)itemAndCommentDataList.get(position)).setMy_like(root.getInt("my_like"));
            detailActivityWithCommentAndReport.updateAnItem(position);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

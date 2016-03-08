package com.wicam.d_default_lecture;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
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
public class LectureAssessAsyncTask extends AsyncTask<String, integer, String> {

    DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    ArrayList<ItemAndCommentData> detailAndCommentList;

    public LectureAssessAsyncTask(DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, ArrayList<ItemAndCommentData> detailAndCommentList) {
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.detailAndCommentList = detailAndCommentList;
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
                if (num_results <= (page + 1) * unit)
                    detailActivityWithCommentAndReport.setDownloadedAllInServer(true);

                JSONArray cmntArray = root.getJSONArray("results");
                for (int i = 0; i < cmntArray.length(); i++) {
                    JSONObject cmnt = cmntArray.getJSONObject(i);
                    detailAndCommentList.add(
                            new LectureAssessData(cmnt.getString("lecture_assess_id"), cmnt.getString("item_id"), cmnt.getString("writer_id"), cmnt.getString("write_time"),
                                    cmnt.getInt("difficulty"), cmnt.getInt("instructiveness"), cmnt.getString("description"),
                                    (i == cmntArray.length() - 1) && (num_results > ((page + 1) * unit)), false)

                    );
                }

            }
            else {
                detailAndCommentList.add(new LectureAssessData("", "", "", "", 0, 0, "", false, true));
                detailActivityWithCommentAndReport.noComment = true;
            }

            Singleton.create().setCanBindView(true);
            detailActivityWithCommentAndReport.showDownloadedList();
            detailActivityWithCommentAndReport.setDownloading(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

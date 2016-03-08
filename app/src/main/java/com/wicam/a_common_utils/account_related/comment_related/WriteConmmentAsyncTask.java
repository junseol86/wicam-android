package com.wicam.a_common_utils.account_related.comment_related;

import android.R.integer;
import android.os.AsyncTask;

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
public class WriteConmmentAsyncTask extends AsyncTask<String, integer, String> {

    WriteCommentActivity writeCommentActivity;

    public WriteConmmentAsyncTask(WriteCommentActivity writeCommentActivity) {
        this.writeCommentActivity = writeCommentActivity;
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
            writeCommentActivity.comment_id = root.getString("comment_id");

            // 댓글을 올리고 상세화면으로 돌아갈 시 댓글 수와 오류제보 수가 업데이트되도록 아이템정보 변경
            Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).setReports(root.getInt("reports"));
            Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).setComments(root.getInt("comments"));

            Singleton.create().setRefreshDashboard(true);

            writeCommentActivity.afterSubmittingContent();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

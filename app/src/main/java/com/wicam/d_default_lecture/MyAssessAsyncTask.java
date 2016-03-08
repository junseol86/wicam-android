package com.wicam.d_default_lecture;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.account_related.comment_related.WriteCommentActivity;
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
public class MyAssessAsyncTask extends AsyncTask<String, integer, String> {

    LectureDetailActivity lectureDetailActivity;

    public MyAssessAsyncTask(LectureDetailActivity lectureDetailActivity) {
        this.lectureDetailActivity = lectureDetailActivity;
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

            // 댓글을 올리고 상세화면으로 돌아갈 시 댓글 수와 오류제보 수가 업데이트되도록 아이템정보 변경
            LectureData lectureData = (LectureData)Singleton.create().getItemDataList().get(Singleton.create().getItemPosition());
            lectureData.setAvgDifficulty(root.getDouble("average_difficulty"));
            lectureData.setAvgInstructiveness(root.getDouble("average_instructiveness"));
            lectureData.setMyDifficulty(root.getInt("my_difficulty"));
            lectureData.setMyInstructiveness(root.getInt("my_instructiveness"));
            lectureData.setMyDescription(root.getString("my_description"));

            Singleton.create().setRefreshDashboard(true);
            lectureDetailActivity.contentsRefresh();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

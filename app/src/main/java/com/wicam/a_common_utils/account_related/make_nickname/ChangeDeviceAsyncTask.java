package com.wicam.a_common_utils.account_related.make_nickname;

import android.R.integer;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.wicam.a_common_utils.common_values.MyCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hyeonmin on 2015-04-13.
 */
public class ChangeDeviceAsyncTask extends AsyncTask<String, integer, String> {

    private MakeNicknameActivity makeNicknameActivity;

    public ChangeDeviceAsyncTask(MakeNicknameActivity makeNicknameActivity) {
        this.makeNicknameActivity = makeNicknameActivity;
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
            if (!root.getString("id").equalsIgnoreCase("0")) {
                new MyCache(makeNicknameActivity).setMyId(root.getString("id"));
                new MyCache(makeNicknameActivity).setMyNickname(String.valueOf(root.getString("nickname")));
                makeNicknameActivity.goToNicknameNeededActivity(1);
            }
            else {
                new AlertDialog.Builder(makeNicknameActivity)
                        .setTitle("닉네임과 패스워드를 확인해주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

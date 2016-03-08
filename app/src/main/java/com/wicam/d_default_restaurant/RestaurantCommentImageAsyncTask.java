package com.wicam.d_default_restaurant;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_restaurant.detail_page.RestaurantDetailActivity;

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
public class RestaurantCommentImageAsyncTask extends AsyncTask<String, integer, String> {

    // 식당의 리스트, 상세 페이지는 사진댓글에 영향을 받으므로 댓글이 쓰이거나 지워질 때마다 업데이트

    RestaurantDetailActivity restaurantDetailActivity;

    public RestaurantCommentImageAsyncTask(RestaurantDetailActivity restaurantDetailActivity) {
        this.restaurantDetailActivity = restaurantDetailActivity;
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

            ArrayList<String> photos = new ArrayList<String>();
            JSONArray photoArray = root.getJSONArray("photo_list");
            for (int j = 0; j < photoArray.length(); j++)
                photos.add(photoArray.getString(j));

            String main_photo = root.getString("main_photo");

            ((RestaurantData) Singleton.create().getItemDataList().get(Singleton.create().getItemPosition())).setPhotoList(photos);
            ((RestaurantData)Singleton.create().getItemDataList().get(Singleton.create().getItemPosition())).setMainPhoto(main_photo);
            ((RestaurantData)Singleton.create().getItemDataList().get(Singleton.create().getItemPosition())).setImage(null);
            restaurantDetailActivity.afterPhotoListDownloaded();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

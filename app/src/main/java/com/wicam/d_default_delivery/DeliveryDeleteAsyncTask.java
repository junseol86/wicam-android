package com.wicam.d_default_delivery;

import android.R.integer;
import android.os.AsyncTask;

import com.wicam.d_default_delivery.detail_page.DeliveryDetailActivity;
import com.wicam.d_default_restaurant.detail_page.RestaurantDetailActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hyeonmin on 2015-04-13.
 */
public class DeliveryDeleteAsyncTask extends AsyncTask<String, integer, String> {

    DeliveryDetailActivity deliveryDetailActivity;

    public DeliveryDeleteAsyncTask(DeliveryDetailActivity deliveryDetailActivity) {
        this.deliveryDetailActivity = deliveryDetailActivity;
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

            JSONObject jo = new JSONObject(str);

            if (jo.getString("result").equalsIgnoreCase("success"))
                deliveryDetailActivity.afterDeliveryDeleted();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}

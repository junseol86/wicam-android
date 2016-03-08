package com.wicam.d_default_restaurant.detail_page;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wicam.a_common_utils.common_values.Security;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Hyeonmin on 2015-07-21.
 */
public class RestaurantDetailPagerImageTask extends AsyncTask{
    private RestaurantPhotoVPAdapter adapter;
    private ImageView image_view;
    private String comment_id;
    private Bitmap bitmap;
    private RelativeLayout viewPagerContainer;

    public RestaurantDetailPagerImageTask(RestaurantPhotoVPAdapter adapter, ImageView image_view, String comment_id, RelativeLayout viewPagerContainer){
        this.adapter = adapter;
        this.image_view = image_view;
        this.comment_id = comment_id;
        this.viewPagerContainer = viewPagerContainer;
    }


    @Override
    protected void onPostExecute(Object result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

        int ivWidth = viewPagerContainer.getWidth();
        int ivHeight = viewPagerContainer.getHeight();
        int picWidth = bitmap.getWidth();
        int picHeight = bitmap.getHeight();
        Bitmap croppedImage;

        if ((double)picWidth / (double)picHeight > (double)ivWidth / (double)ivHeight) {
            int width, height;
            height = picHeight;
            width = (int)((double)picHeight * (double)ivWidth / (double)ivHeight);
            croppedImage = Bitmap.createBitmap(bitmap, (picWidth-width)/2, 0, width, height);
        }
        else {
            int width, height;
            width = picWidth;
            height = (int)((double)picWidth * (double)ivHeight / (double)ivWidth);
            croppedImage = Bitmap.createBitmap(bitmap, 0, (picHeight-height)/2, width, height);

        }

        image_view.setImageBitmap(croppedImage);
        adapter.notifyDataSetChanged();
    }

    class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int bite = read();

                    if (bite < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1;   // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    @Override
    protected Object doInBackground(Object... params) {
        try{
            InputStream bis;
            String image_url = new Security().RESTAURANT_IMAGE;
            bis = new java.net.URL(image_url + comment_id + ".jpg").openStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = BitmapFactory.decodeStream(new FlushedInputStream(bis),null,options);
            bis.close();

        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
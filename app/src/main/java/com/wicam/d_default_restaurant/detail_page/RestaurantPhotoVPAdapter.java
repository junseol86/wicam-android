package com.wicam.d_default_restaurant.detail_page;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wicam.R;
import com.wicam.d_default_restaurant.RestaurantData;

/**
 * Created by Hyeonmin on 2015-07-21.
 */
public class RestaurantPhotoVPAdapter extends PagerAdapter {

    private Context context;
    private RestaurantData restaurantDetailData;
    private LayoutInflater layoutInflater;
    private RelativeLayout viewPagerContainer;

    public RestaurantPhotoVPAdapter(Context context, RestaurantData restaurantDetailData, RelativeLayout viewPagerContainer) {
        this.context = context;
        this.restaurantDetailData = restaurantDetailData;
        this.layoutInflater = LayoutInflater.from(context);
        this.viewPagerContainer = viewPagerContainer;
    }

    @Override
    public int getCount() {
        return restaurantDetailData.getPhotoList().size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        ImageView imageView = new ImageView(context);
        RestaurantDetailPagerImageTask image_task = new RestaurantDetailPagerImageTask(this, imageView, restaurantDetailData.getPhotoList().get(position), viewPagerContainer);
        image_task.execute();
        RelativeLayout rl = (RelativeLayout)layoutInflater.inflate(R.layout.d_restaurant_detail_pics_pager_rl, null);
        rl.addView(imageView, lp);
        ((ViewPager) container).addView(rl, 0);
        return rl;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout)object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}

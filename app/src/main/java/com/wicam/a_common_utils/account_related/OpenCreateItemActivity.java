package com.wicam.a_common_utils.account_related;

import android.app.Activity;
import android.content.Intent;

import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.d_default_advertise.AdvertiseAddModifyActivity;
import com.wicam.d_default_custom.CustomAddModifyActivity;
import com.wicam.d_default_delivery.DeliveryAddModifyActivity;
import com.wicam.d_default_phonebook.PhonebookAddModifyActivity;
import com.wicam.d_default_restaurant.RestaurantAddModifyActivity;

/**
 * Created by Hyeonmin on 2015-07-27.
 */
public class OpenCreateItemActivity {

    public OpenCreateItemActivity() {
    }

    public void execute(Activity activity) {
        switch (new MyCache(activity).getDefaultCode()) {
            case "0":
                activity.startActivityForResult(new Intent(activity, CustomAddModifyActivity.class), 0);
                break;
            case "1":
                activity.startActivityForResult(new Intent(activity, DeliveryAddModifyActivity.class), 0);
                break;
            case "2":
                activity.startActivityForResult(new Intent(activity, RestaurantAddModifyActivity.class), 0);
                break;
            case "4":
                activity.startActivityForResult(new Intent(activity, PhonebookAddModifyActivity.class), 0);
                break;
            case "5":
                activity.startActivityForResult(new Intent(activity, AdvertiseAddModifyActivity.class), 0);
                break;
        }
    }
}

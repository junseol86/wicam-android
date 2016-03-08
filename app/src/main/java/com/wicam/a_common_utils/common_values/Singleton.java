package com.wicam.a_common_utils.common_values;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.c_main_page.content_drawer.ContentListData;
import com.wicam.d_default_advertise.AdvertiseData;
import com.wicam.d_default_custom.CustomData;
import com.wicam.d_default_delivery.DeliveryData;
import com.wicam.d_default_phonebook.PhonebookData;
import com.wicam.d_default_restaurant.RestaurantData;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-09.
 */
public class Singleton {

    private static Singleton singleton;

    private static int out_all_in = 1; // 한동대 버스시간표 모드
    private static boolean canBindView = false; // 필터를 빨리 바꾸거나 할 때 RecyclerView에서 에러가 나지 않도록, 리스트를 다 받은 후에 true로
    private static int itemPosition;
    private static double latitude, longitude;
    private static String markerName;
    private static boolean scrolling = false; // 리스트뷰가 스크롤중에는 바뀌지 않도록 하여 속도를 늘림
    private static int addOrModify; // 아이템을 더하는지 수정하는지 여부.  0:추가, 1:수정
    private static ContentListData contentListData;
    private static CustomData customData;
    private static RestaurantData restaurantData;
    private static DeliveryData deliveryData;
    private static PhonebookData phonebookData;
    private static AdvertiseData advertiseData;
    private static ArrayList<String> schoolIdList;
    private static String itemId = ""; // ""이 아니라면 대시보드에서 게시물을 클릭하여 해당 컨텐츠로 들어간다.
    private static boolean refreshDashboard = false;

    private static ArrayList<ItemData> itemDataList;

    public static Singleton create() {
        if (singleton != null)
            return singleton;
        else
            return new Singleton();
    }


    public static void setSingleton(Singleton singleton) {
        Singleton.singleton = singleton;
    }


    public static int getOut_all_in() {
        return out_all_in;
    }

    public static void setOut_all_in(int out_all_in) {
        Singleton.out_all_in = out_all_in;
    }

    public static boolean isCanBindView() {
        return canBindView;
    }

    public static void setCanBindView(boolean canBindView) {
        Singleton.canBindView = canBindView;
    }

    public int getItemPosition() {
        return itemPosition;
    }

    public void setItemPosition(int itemPosition) {
        Singleton.itemPosition = itemPosition;
    }

    public static ArrayList<ItemData> getItemDataList() {
        return itemDataList;
    }

    public static void setItemDataList(ArrayList<ItemData> itemDataList) {
        Singleton.itemDataList = itemDataList;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        Singleton.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        Singleton.longitude = longitude;
    }

    public static String getMarkerName() {
        return markerName;
    }

    public static void setMarkerName(String markerName) {
        Singleton.markerName = markerName;
    }

    public static boolean isScrolling() {
        return scrolling;
    }

    public static void setScrolling(boolean scrolling) {
        Singleton.scrolling = scrolling;
    }

    public static int getAddOrModify() {
        return addOrModify;
    }

    public static void setAddOrModify(int addOrModify) {
        Singleton.addOrModify = addOrModify;
    }

    public static ContentListData getContentListData() {
        return contentListData;
    }

    public static CustomData getCustomData() {
        return customData;
    }

    public static void setCustomData(CustomData customData) {
        Singleton.customData = customData;
    }

    public static void setContentListData(ContentListData contentListData) {
        Singleton.contentListData = contentListData;
    }

    public RestaurantData getRestaurantData() {
        return restaurantData;
    }

    public void setRestaurantData(RestaurantData restaurantData) {
        Singleton.restaurantData = restaurantData;
    }

    public ArrayList<String> getSchoolIdList() {
        return schoolIdList;
    }

    public void setSchoolIdList(ArrayList<String> schoolIdList) {
        Singleton.schoolIdList = schoolIdList;
    }

    public static Singleton getSingleton() {
        return singleton;
    }

    public static DeliveryData getDeliveryData() {
        return deliveryData;
    }

    public static void setDeliveryData(DeliveryData deliveryData) {
        Singleton.deliveryData = deliveryData;
    }

    public static PhonebookData getPhonebookData() {
        return phonebookData;
    }

    public static void setPhonebookData(PhonebookData phonebookData) {
        Singleton.phonebookData = phonebookData;
    }

    public static AdvertiseData getAdvertiseData() {
        return advertiseData;
    }

    public static void setAdvertiseData(AdvertiseData advertiseData) {
        Singleton.advertiseData = advertiseData;
    }

    public static String getItemId() {
        return itemId;
    }

    public static void setItemId(String itemId) {
        Singleton.itemId = itemId;
    }

    public static boolean isRefreshDashboard() {
        return refreshDashboard;
    }

    public static void setRefreshDashboard(boolean refreshDashboard) {
        Singleton.refreshDashboard = refreshDashboard;
    }
}

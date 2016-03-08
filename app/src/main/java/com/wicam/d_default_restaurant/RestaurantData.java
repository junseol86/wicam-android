package com.wicam.d_default_restaurant;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class RestaurantData extends ItemData {
    private String genre, description, address, latitude, longitude, phone1, phone2;
    private ArrayList<String> photoList;
    private String mainPhoto;

    public RestaurantData(ArrayList<String> schoolIdList, ArrayList<String> schoolNameList,
                        String itemId, String itemName, String genre, String description, String address, String latitude, String longitude,
                          String phone1, String phone2, String writerId, String modifierId, String modifierNickname, String modifyTime,
                          int comments, int reports,
                          int myFavorite, int likes, int myLike, int authority, ArrayList<String> photoList, String mainPhoto, boolean toShowLoading, boolean empty) {

        this.schoolIdList = schoolIdList;
        this.schoolNameList = schoolNameList;
        this.itemId = itemId;
        this.itemName = itemName;
        this.genre = genre;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.writerId = writerId;
        this.modifierId = modifierId;
        this.modifierNickname = modifierNickname;
        this.modifyTime = modifyTime;
        this.comments = comments;
        this.reports = reports;
        this.myFavorite = myFavorite;
        this.likes = likes;
        this.myLike = myLike;
        this.authority = authority;
        this.photoList = photoList;
        this.mainPhoto = mainPhoto;
        this.toShowLoading = toShowLoading;
        this.empty = empty;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }


    public ArrayList<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<String> photoList) {
        this.photoList = photoList;
    }

    public String getMainPhoto() {
        return mainPhoto;
    }

    public void setMainPhoto(String mainPhoto) {
        this.mainPhoto = mainPhoto;
    }

}

package com.wicam.d_default_delivery;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class DeliveryData extends ItemData {
    private String includes, deliveryCondition, description, menu, phone1, phone2;
    private int hasPhoto;

    public DeliveryData(ArrayList<String> schoolIdList, ArrayList<String> schoolNameList,
                        String itemId, String itemName, String includes, String deliveryCondition, String description, String menu,
                        String phone1, String phone2, String writerId, String modifierId, String modifierNickname, String modifyTime, int hasPhoto,
                        int comments, int reports,
                        int myFavorite, int likes, int myLike, int authority, boolean toShowLoading, boolean empty) {

        this.schoolIdList = schoolIdList;
        this.schoolNameList = schoolNameList;
        this.itemId = itemId;
        this.itemName = itemName;
        this.includes = includes;
        this.deliveryCondition = deliveryCondition;
        this.description = description;
        this.menu = menu;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.writerId = writerId;
        this.modifierId = modifierId;
        this.modifierNickname = modifierNickname;
        this.modifyTime = modifyTime;
        this.hasPhoto = hasPhoto;
        this.comments = comments;
        this.reports = reports;
        this.myFavorite = myFavorite;
        this.likes = likes;
        this.myLike = myLike;
        this.authority = authority;
        this.toShowLoading = toShowLoading;
        this.empty = empty;
    }

    public String getDescription() {
        return description;
    }

    public String getIncludes() {
        return includes;
    }

    public String getMenu() {
        return menu;
    }

    public String getDeliveryCondition() {
        return deliveryCondition;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public int getHasPhoto() {
        return hasPhoto;
    }
}

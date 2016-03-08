package com.wicam.d_default_phonebook;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-08-04.
 */
public class PhonebookData extends ItemData {
    private String kind, urlLink, description, phone1, phone2;
    private int hasPhoto;

    public PhonebookData(ArrayList<String> schoolIdList, ArrayList<String> schoolNameList,
                        String itemId, String itemName, String kind, String urlLink, String description,
                        String phone1, String phone2, String writerId, String modifierId, String modifierNickname, String modifyTime, int hasPhoto,
                        int comments, int reports,
                        int myFavorite, int likes, int myLike, int authority, boolean toShowLoading, boolean empty) {

        this.schoolIdList = schoolIdList;
        this.schoolNameList = schoolNameList;
        this.itemId = itemId;
        this.itemName = itemName;
        this.kind = kind;
        this.urlLink = urlLink;
        this.description = description;
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

    public String getKind() {
        return kind;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public String getDescription() {
        return description;
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

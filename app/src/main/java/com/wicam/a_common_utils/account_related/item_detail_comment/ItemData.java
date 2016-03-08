package com.wicam.a_common_utils.account_related.item_detail_comment;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-23.
 */
public class ItemData extends ItemAndCommentData {
    protected ArrayList<String> schoolIdList, schoolNameList;
    protected String itemId, itemName, writerId, modifierId, modifierNickname, modifyTime;
    protected int comments, reports, myFavorite, likes, myLike, authority;

    public ArrayList<String> getSchoolIdList() {
        return schoolIdList;
    }

    public ArrayList<String> getSchoolNameList() {
        return schoolNameList;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getReports() {
        return reports;
    }

    public void setReports(int reports) {
        this.reports = reports;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getModifierId() {
        return modifierId;
    }

    public String getModifierNickname() {
        return modifierNickname;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public int getMyFavorite() {
        return myFavorite;
    }

    public void setMyFavorite(int myFavorite) {
        this.myFavorite = myFavorite;
    }

    public int getMyLike() {
        return myLike;
    }

    public void setMyLike(int myLike) {
        this.myLike = myLike;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getAuthority() {
        return authority;
    }
}

package com.wicam.c_main_page.content_drawer;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-13.
 */
public class ContentListData {
    protected ArrayList<String> schoolIdList, schoolNameList;
    private String content_id, default_code, content_name, content_type, description, url_link, package_name, contact, writer_id, modifier_nickname, modify_time;
    private int favorite;
    private boolean toShowLoading;

    public ContentListData(ArrayList<String> schoolIdList, ArrayList<String> schoolNameList,
                        String content_id, String default_code, String content_name, String content_type, String description,
                           String url_link, String package_name, String contact, String writer_id, String modifier_nickname,
                           String modify_time, int favorite, boolean toShowLoading) {
        this.schoolIdList = schoolIdList;
        this.schoolNameList = schoolNameList;
        this.content_id = content_id;
        this.default_code = default_code;
        this.content_name = content_name;
        this.description = description;
        this.content_type = content_type;
        this.url_link = url_link;
        this.package_name = package_name;
        this.contact = contact;
        this.writer_id = writer_id;
        this.modifier_nickname = modifier_nickname;
        this.modify_time = modify_time;
        this.favorite = favorite;
        this.toShowLoading = toShowLoading;
    }

    public ArrayList<String> getSchoolIdList() {
        return schoolIdList;
    }

    public ArrayList<String> getSchoolNameList() {
        return schoolNameList;
    }

    public String getContent_id() {
        return content_id;
    }

    public String getDefault_code() {
        return default_code;
    }

    public String getContent_name() {
        return content_name;
    }

    public String getDescription() {
        return description;
    }

    public String getModify_time() {
        return modify_time;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getContent_type() {
        return content_type;
    }

    public String getUrl_link() {
        return url_link;
    }

    public String getPackage_name() {
        return package_name;
    }

    public String getContact() {
        return contact;
    }

    public String getWriter_id() {
        return writer_id;
    }

    public String getModifier_nickname() {
        return modifier_nickname;
    }

    public boolean isToShowLoading() {
        return toShowLoading;
    }

    public void setToShowLoading(boolean toShowLoading) {
        this.toShowLoading = toShowLoading;
    }

}

package com.wicam.c_main_page.dashboard;

import android.graphics.Bitmap;

import com.wicam.a_common_utils.scroll_to_update.ImageContainingData;

/**
 * Created by Hyeonmin on 2015-08-13.
 */
public class DashboardData extends ImageContainingData {

    private String idx, content_idx, photo_idx, title, text, person, value1, value2, time, content, school;
    private int hasPic;

    public DashboardData(String idx, String content_idx, String photo_idx, String title, String text, String person,
                         String value1, String value2, String time, String content, String school,
                         int hasPic, boolean toShowLoading, boolean empty) {
        this.idx = idx;
        this.content_idx = content_idx;
        this.photo_idx = photo_idx;
        this.title = title;
        this.text = text;
        this.person = person;
        this.value1 = value1;
        this.value2 = value2;
        this.time = time;
        this.content = content;
        this.school = school;
        this.hasPic = hasPic;
        this.toShowLoading = toShowLoading;
        this.empty = empty;
    }

    public String getIdx() {
        return idx;
    }

    public String getContent_idx() {
        return content_idx;
    }

    public String getPhoto_idx() {
        return photo_idx;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getPerson() {
        return person;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }

    public String getSchool() {
        return school;
    }

    public int getHasPic() {
        return hasPic;
    }
}

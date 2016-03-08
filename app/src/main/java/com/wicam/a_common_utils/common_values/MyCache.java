package com.wicam.a_common_utils.common_values;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hyeonmin on 2015-07-23.
 */
public class MyCache {
    // SharedPreference 값을 쉽게 넣고 빼기 위한 클래스

    Activity activity;
    SharedPreferences sharedPreferences;
    SharedPreferences deviceSharedPreferences;

    public MyCache(Activity activity) {
        activity = activity;
        sharedPreferences = activity.getSharedPreferences("wicam", Context.MODE_PRIVATE);
        deviceSharedPreferences = activity.getSharedPreferences("wicam_id", Context.MODE_PRIVATE);
    }

    public String getMyId() {
        return sharedPreferences.getString("my_id", "");
    }
    public void setMyId(String string) {
        sharedPreferences.edit().putString("my_id", string).commit();
    }

    public String getMyNickname() {
        return sharedPreferences.getString("my_nickname", "");
    }
    public void setMyNickname(String string) {
        sharedPreferences.edit().putString("my_nickname", string).commit();
    }
    public String getDefaultCode() {
        return sharedPreferences.getString("default_code", "");
    }
    public void setDefaultCode(String string) {
        sharedPreferences.edit().putString("default_code", string).commit();
    }

    public String getContentId() {
        return sharedPreferences.getString("content_id", "");
    }
    public void setContentId(String string) {
        sharedPreferences.edit().putString("content_id", string).commit();
    }

    public String getContentType() {
        return sharedPreferences.getString("content_type", "");
    }
    public void setContentType(String string) {
        sharedPreferences.edit().putString("content_type", string).commit();
    }

    public String getDeviceId() {
        return deviceSharedPreferences.getString("device_id", "");
    }

    public void setAuthority(String string) {
        deviceSharedPreferences.edit().putString("user_memo", string).commit();
    }

    public String getAuthority() {
        return deviceSharedPreferences.getString("user_memo", "");
    }

    public void setBlackList(int blacklist) {
        deviceSharedPreferences.edit().putInt("blacklist", blacklist).commit();
    }

    public int getBlackList() {
        return deviceSharedPreferences.getInt("blacklist", 0);
    }

    public void setMySchoolId(String string) {
        sharedPreferences.edit().putString("school_id", string).commit();
    }

    public String getMySchoolId() {
        return sharedPreferences.getString("school_id", "");
    }

    public void clearSchool() {
        sharedPreferences.edit().remove("school_id").commit();
    }

    public void setMySchoolName(String string) {
        sharedPreferences.edit().putString("school_name", string).commit();
    }

    public String getMySchoolName() {
        return sharedPreferences.getString("school_name", "");
    }

    public void setNicknameNeededTaskCode(int code) {
        sharedPreferences.edit().putInt("nickname_needed_task_code", code).commit();
    }

    public int getNicknameNeededTaskCode() {
        return sharedPreferences.getInt("nickname_needed_task_code", 9);
    }
}

package com.wicam.d_default_lecture;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-08-04.
 */
public class LectureData extends ItemData {
    private String professorName, major;
    private double avgDifficulty, avgInstructiveness;
    private int myDifficulty, myInstructiveness;
    private String myDescription;
    private int hasPhoto;

    public LectureData(
                       String itemId, String itemName, String professorName, String major, double avgDifficulty, double avgInstructiveness,
                       int myDifficulty, int myInstructiveness, String myDescription,
                       String writerId, String modifyTime,
                       int authority, boolean toShowLoading, boolean empty) {

        this.itemId = itemId;
        this.itemName = itemName;
        this.professorName = professorName;
        this.major = major;
        this.avgDifficulty = avgDifficulty;
        this.avgInstructiveness = avgInstructiveness;
        this.myDifficulty = myDifficulty;
        this.myInstructiveness = myInstructiveness;
        this.myDescription = myDescription;
        this.writerId = writerId;
        this.modifyTime = modifyTime;
        this.authority = authority;
        this.toShowLoading = toShowLoading;
        this.empty = empty;
    }

    public double getAvgInstructiveness() {
        return avgInstructiveness;
    }

    public void setAvgInstructiveness(double avgInstructiveness) {
        this.avgInstructiveness = avgInstructiveness;
    }

    public double getAvgDifficulty() {
        return avgDifficulty;
    }

    public void setAvgDifficulty(double avgDifficulty) {
        this.avgDifficulty = avgDifficulty;
    }

    public int getMyDifficulty() {
        return myDifficulty;
    }

    public void setMyDifficulty(int myDifficulty) {
        this.myDifficulty = myDifficulty;
    }

    public int getMyInstructiveness() {
        return myInstructiveness;
    }

    public void setMyInstructiveness(int myInstructiveness) {
        this.myInstructiveness = myInstructiveness;
    }

    public String getMyDescription() {
        return myDescription;
    }

    public void setMyDescription(String myDescription) {
        this.myDescription = myDescription;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }
}

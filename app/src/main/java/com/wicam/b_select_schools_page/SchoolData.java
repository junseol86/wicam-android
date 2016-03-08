package com.wicam.b_select_schools_page;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class SchoolData {
    private String schoolId, schoolName, schoolContents;

    public SchoolData(String schoolId, String schoolName, String schoolContents) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.schoolContents = schoolContents;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getSchoolContents() {
        return schoolContents;
    }
}

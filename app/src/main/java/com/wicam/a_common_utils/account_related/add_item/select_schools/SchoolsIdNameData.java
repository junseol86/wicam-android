package com.wicam.a_common_utils.account_related.add_item.select_schools;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class SchoolsIdNameData {
    private String schoolId, schoolName;

    public SchoolsIdNameData(String schoolId, String schoolName) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

}

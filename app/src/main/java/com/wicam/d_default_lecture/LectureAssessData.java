package com.wicam.d_default_lecture;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class LectureAssessData extends ItemAndCommentData {
    // 댓글만을 포함하는 데이터

    private String lectureAssessId, lectureId, writerId, writeTime, description;
    private int difficulty, instructiveness;

    public LectureAssessData(String lectureAssessId, String lectureId, String writerId, String writeTime, int difficulty, int instructiveness, String description,
                             boolean toShowLoading, boolean empty) {

        this.lectureAssessId = lectureAssessId;
        this.lectureId = lectureId;
        this.writerId = writerId;
        this.writeTime = writeTime;
        this.difficulty = difficulty;
        this.instructiveness = instructiveness;
        this.description = description;
        this.toShowLoading = toShowLoading;
        this.empty = empty;
    }

    public String getLectureAssessId() {
        return lectureAssessId;
    }

    public String getLectureId() {
        return lectureId;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getWriteTime() {
        return writeTime;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getInstructiveness() {
        return instructiveness;
    }

    public String getDescription() {
        return description;
    }

    public boolean isToShowLoading() {
        return toShowLoading;
    }

    public boolean isEmpty() {
        return empty;
    }
}

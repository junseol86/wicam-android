package com.wicam.a_common_utils.scroll_to_update;

import android.graphics.Bitmap;

/**
 * Created by Hyeonmin on 2015-07-16.
 */
public class ImageContainingData {
    private Bitmap image;
    protected boolean toShowLoading; // 서버의 리스트를 다 받지 않은 상태에서 스크롤을 맨 밑으로 내렸을 때, 더 받아오는 동안 아래 로딩을 띄울 항목인지 여부
    protected boolean empty;
    protected int report;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isToShowLoading() {
        return toShowLoading;
    }

    public void setToShowLoading(boolean toShowLoading) {
        this.toShowLoading = toShowLoading;
    }
}

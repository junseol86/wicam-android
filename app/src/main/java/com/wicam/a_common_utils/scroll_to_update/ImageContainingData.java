package com.wicam.a_common_utils.scroll_to_update;

import android.graphics.Bitmap;

/**
 * Created by Hyeonmin on 2015-07-16.
 */
public class ImageContainingData {
    private Bitmap image;
    protected boolean toShowLoading; // ������ ����Ʈ�� �� ���� ���� ���¿��� ��ũ���� �� ������ ������ ��, �� �޾ƿ��� ���� �Ʒ� �ε��� ��� �׸����� ����
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

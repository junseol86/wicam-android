package com.wicam.a_common_utils.account_related.item_detail_comment;

import com.wicam.a_common_utils.scroll_to_update.ActivityWithScrollUpdate;

/**
 * Created by Hyeonmin on 2015-07-23.
 */
public abstract class ListActivityShowingLikesAndComments extends ActivityWithScrollUpdate {
    // ������Ŭ������ �����۵鿡 ���ƿ�� ��� ���� ǥ�õǴ� ������

    @Override
    public void getMoreList() {
        page++;
        getList("");
    }

    abstract public void getList(String itemId);
    abstract public void getNewList(String itemId);
}

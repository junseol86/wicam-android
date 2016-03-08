package com.wicam.a_common_utils.account_related.item_detail_comment;

import com.wicam.a_common_utils.scroll_to_update.ActivityWithScrollUpdate;

/**
 * Created by Hyeonmin on 2015-07-23.
 */
public abstract class ListActivityShowingLikesAndComments extends ActivityWithScrollUpdate {
    // 리사이클러뷰의 아이템들에 좋아요와 댓글 수가 표시되는 페이지

    @Override
    public void getMoreList() {
        page++;
        getList("");
    }

    abstract public void getList(String itemId);
    abstract public void getNewList(String itemId);
}

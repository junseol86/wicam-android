package com.wicam.a_common_utils.account_related.comment_related;


import android.content.Intent;

import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.scroll_to_update.ActivityWithScrollUpdate;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-21.
 */
public abstract class DetailActivityWithCommentAndReport extends ActivityWithScrollUpdate {

    // ��� �ޱ�� �������Ű� ��ư�� �ִ� �� ������

    protected CommentAndReport commentAndReport;
    protected ItemData itemData;
    protected ArrayList<ItemAndCommentData> itemAndCommentDataList = new ArrayList<ItemAndCommentData>();
    public int reportOnly = 0;
    public boolean noComment = false;

    public void contentsRefresh() {
        page = 0;
        downloadedAllInServer = false;
        progressDialog.show();
        if (itemAndCommentDataList != null)
            itemAndCommentDataList.removeAll(itemAndCommentDataList);
        itemData = Singleton.create().getItemDataList().get(Singleton.create().getItemPosition());
        itemAndCommentDataList.add(itemData);
        setPageContents();
        setCommentAndReportButtons();
        downloadComments();
    }

    public void setCommentAndReportButtons() {
        commentAndReport = new CommentAndReport(this, (ItemData)itemAndCommentDataList.get(0));
    }

    @Override
    public void getMoreList() {
        page++;
        downloadComments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) { // ��� �ۼ� �� ȭ�� refresh
            afterCommentWorkWithImage();
        }
        else if (requestCode == 1) {
            if (resultCode == 1) // �������� �� ������ ���� ������ �����
                afterItemModified(data.getStringExtra("itemId"));
        }
    }

    protected void afterCommentWorkWithImage() {
        contentsRefresh();
    }
    protected abstract void afterItemModified(String itemId);

    abstract public void downloadComments();
    abstract public void setPageContents();

}

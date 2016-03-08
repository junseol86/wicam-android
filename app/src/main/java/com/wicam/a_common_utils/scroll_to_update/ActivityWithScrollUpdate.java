package com.wicam.a_common_utils.scroll_to_update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Singleton;

/**
 * Created by Hyeonmin on 2015-07-22.
 */
public abstract class ActivityWithScrollUpdate extends Activity {
    // 스크롤하여 리사이클러뷰의 목록을 갱신하는 페이지

    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected int page;
    protected ProgressDialog progressDialog;
    protected boolean downloading = false; // 항목을 다운로드하고 있는지 여부 (스크롤 끝에서 getMoreList가 여러번 실행되지 않도록)
    protected boolean downloadedAllInServer = false; // 모든 항목을 다 다운로드하여, 스크롤 끝에서도 더 이상 getMoreList를 실행하지 않아도 되는지 여부
    protected boolean scrolling = false;

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public void setDownloadedAllInServer(boolean downloadedAllInServer) {
        this.downloadedAllInServer = downloadedAllInServer;
    }

    public abstract void showDownloadedList();
    abstract public void getMoreList();
    abstract public void updateAnItem(int position);

}

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
    // ��ũ���Ͽ� ������Ŭ������ ����� �����ϴ� ������

    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    protected int page;
    protected ProgressDialog progressDialog;
    protected boolean downloading = false; // �׸��� �ٿ�ε��ϰ� �ִ��� ���� (��ũ�� ������ getMoreList�� ������ ������� �ʵ���)
    protected boolean downloadedAllInServer = false; // ��� �׸��� �� �ٿ�ε��Ͽ�, ��ũ�� �������� �� �̻� getMoreList�� �������� �ʾƵ� �Ǵ��� ����
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

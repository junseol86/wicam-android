package com.wicam.d_default_phonebook.detail_page;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.ItemFavoriteAsyncTask;
import com.wicam.a_common_utils.ItemLikeAsyncTask;
import com.wicam.a_common_utils.WebViewPage;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemCommentAsyncTask;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.d_default_phonebook.PhonebookData;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class PhonebookDetailActivity extends DetailActivityWithCommentAndReport {
    private PhonebookDetailAdapter adapter;
    private PhonebookData phonebookDetailData;
    private PhonebookDetailActivity phonebookDetailActivity;
    private boolean scrolling = false;

    private ImageButton favoriteButton;
    private Button likeButton, callButton, linkButton;
    private ImageView likeImage, callImage, linkImage;
    private TextView likeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_phonebook_detail);

        phonebookDetailActivity = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오고 있습니다.");

        favoriteButton = (ImageButton)findViewById(R.id.phonebook_detail_favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemFavoriteAsyncTask(phonebookDetailActivity, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id=" + phonebookDetailData.getItemId() +
                        "&user_id=" + new MyCache(phonebookDetailActivity).getMyId() + "&default_code=" + new MyCache(phonebookDetailActivity).getDefaultCode() +
                        "&content_id=" + new MyCache(phonebookDetailActivity).getContentId() + "&content_type=" + new MyCache(phonebookDetailActivity).getContentType());
            }
        });
        likeButton = (Button)findViewById(R.id.phonebook_detail_like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemLikeAsyncTask(phonebookDetailActivity, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_like_toggle.php?item_id=" + phonebookDetailData.getItemId() +
                        "&user_id=" + new MyCache(phonebookDetailActivity).getMyId() + "&default_code=" + new MyCache(phonebookDetailActivity).getDefaultCode() +
                        "&content_id=" + new MyCache(phonebookDetailActivity).getContentId() + "&content_type=" + new MyCache(phonebookDetailActivity).getContentType());
            }
        });

        linkButton = (Button)findViewById(R.id.phonebook_detail_link_button);
        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhonebookDetailActivity.this.startActivity(new Intent(PhonebookDetailActivity.this, WebViewPage.class).putExtra("url_link", ((PhonebookData) itemAndCommentDataList.get(0)).getUrlLink()));
            }
        });

        callButton = (Button)findViewById(R.id.phonebook_detail_call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tel_count = 0;
                if (!phonebookDetailData.getPhone1().trim().equalsIgnoreCase(""))
                    tel_count++;
                if (!phonebookDetailData.getPhone2().trim().equalsIgnoreCase(""))
                    tel_count++;
                String[] tel = new String[tel_count];
                int tel_pointer = 0;
                if (!phonebookDetailData.getPhone1().trim().equalsIgnoreCase(""))
                    tel[tel_pointer++] = phonebookDetailData.getPhone1().trim();
                if (!phonebookDetailData.getPhone2().trim().equalsIgnoreCase(""))
                    tel[tel_pointer++] = phonebookDetailData.getPhone2().trim();

                if (tel_count > 1) {
                    final String phonebook_tel[] = {tel[0], tel[1]};
                    new AlertDialog.Builder(phonebookDetailActivity).setTitle("번호를 선택하세요")
                            .setItems(phonebook_tel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    String str = phonebook_tel[i];
                                    str = str.replace("-", "");
                                    intent.setData(Uri.parse("tel:" + str));
                                    startActivity(intent);
                                }
                            }).show();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    String str = tel[0];
                    str = str.replace("-", "");
                    intent.setData(Uri.parse("tel:" + str));
                    startActivity(intent);

                }

            }
        });


        recyclerView = (RecyclerView)findViewById(R.id.phonebook_comments_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        findViewById(R.id.comment_and_report).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        findViewById(R.id.comment_and_report).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrolling = false;
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!scrolling)
                                    findViewById(R.id.comment_and_report).setVisibility(View.VISIBLE);
                            }
                        }, 300L);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (((visibleItemCount + firstVisibleItem) >= totalItemCount) && !downloading && !downloadedAllInServer && !noComment) {
                    downloading = true;
                    getMoreList();
                }
            }
        });

        likeImage = (ImageView)findViewById(R.id.phonebook_detail_like_button_image);
        likeNumber = (TextView)findViewById(R.id.phonebook_detail_like_button_number);
        callImage = (ImageView)findViewById(R.id.phonebook_detail_call_button_image);
        linkImage = (ImageView)findViewById(R.id.phonebook_detail_link_button_image);


        contentsRefresh();
        setCommentAndReportButtons();
    }

    @Override
    public void downloadComments() {
        new ItemCommentAsyncTask(this, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_comments.php?page=" + String.valueOf(page) + "&report_only=" + reportOnly
                + "&item_id=" + phonebookDetailData.getItemId() + "&user_id=" + new MyCache(phonebookDetailActivity).getMyId()
                + "&default_code=" + new MyCache(phonebookDetailActivity).getDefaultCode()
                + "&content_id=" + new MyCache(phonebookDetailActivity).getContentId()
                + "&content_type=" + new MyCache(phonebookDetailActivity).getContentType());
    }

    @Override
    public void setPageContents() {
        phonebookDetailData = (PhonebookData)itemData;
        favoriteButton.setBackgroundResource(phonebookDetailData.getMyFavorite() == 1 ? R.drawable.item_detail_favorite_on : R.drawable.item_detail_favorite_off);
        likeImage.setImageResource(phonebookDetailData.getMyLike() == 1 ? R.drawable.item_detail_like_on : R.drawable.item_detail_like_off);
        likeNumber.setText("×" + phonebookDetailData.getLikes());
        callImage.setAlpha(phonebookDetailData.getPhone1().trim().equalsIgnoreCase("") && phonebookDetailData.getPhone2().trim().equalsIgnoreCase("") ?
                0.2f : 1f);
        linkImage.setAlpha(phonebookDetailData.getUrlLink().trim().equalsIgnoreCase("") ?
                0.2f : 1f);
    }

    @Override
    public void updateAnItem(int position) {
        if (position == 0) {
            phonebookDetailData = (PhonebookData) itemAndCommentDataList.get(0);
            setPageContents();
            return;
        }
        adapter.notifyItemChanged(position);
    }

    @Override
    public void showDownloadedList() {
        progressDialog.dismiss();

        if (page == 0) {
            adapter = new PhonebookDetailAdapter(phonebookDetailActivity, this, itemAndCommentDataList);
            recyclerView.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void afterItemModified(String phonebookId) {
        Intent intent = new Intent().putExtra("itemId", phonebookId);
        setResult(1, intent);
        finish();
    }

    public void afterPhonebookDeleted() {
        setResult(2);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
    }

}

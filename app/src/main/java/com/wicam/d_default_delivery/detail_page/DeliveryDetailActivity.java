package com.wicam.d_default_delivery.detail_page;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.ItemFavoriteAsyncTask;
import com.wicam.a_common_utils.ItemLikeAsyncTask;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemCommentAsyncTask;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.d_default_delivery.DeliveryData;
import com.wicam.d_default_delivery.menu_page.DeliveryMenuActivity;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class DeliveryDetailActivity extends DetailActivityWithCommentAndReport {
    private DeliveryDetailAdapter adapter;
    private DeliveryData deliveryDetailData;
    private DeliveryDetailActivity deliveryDetailActivity;
    private boolean scrolling = false;

    private ImageButton favoriteButton;
    private Button likeButton, callButton, menuButton;
    private ImageView likeImage, callImage, menuImage;
    private TextView likeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_delivery_detail);

        deliveryDetailActivity = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오고 있습니다.");

        favoriteButton = (ImageButton)findViewById(R.id.delivery_detail_favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemFavoriteAsyncTask(deliveryDetailActivity, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id=" + deliveryDetailData.getItemId() +
                        "&user_id=" + new MyCache(deliveryDetailActivity).getMyId() + "&default_code=" + new MyCache(deliveryDetailActivity).getDefaultCode() +
                        "&content_id=" + new MyCache(deliveryDetailActivity).getContentId() + "&content_type=" + new MyCache(deliveryDetailActivity).getContentType());
            }
        });
        likeButton = (Button)findViewById(R.id.delivery_detail_like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemLikeAsyncTask(deliveryDetailActivity, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_like_toggle.php?item_id=" + deliveryDetailData.getItemId() +
                        "&user_id=" + new MyCache(deliveryDetailActivity).getMyId() + "&default_code=" + new MyCache(deliveryDetailActivity).getDefaultCode() +
                        "&content_id=" + new MyCache(deliveryDetailActivity).getContentId() + "&content_type=" + new MyCache(deliveryDetailActivity).getContentType());
            }
        });

        callButton = (Button)findViewById(R.id.delivery_detail_call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tel_count = 0;
                if (!deliveryDetailData.getPhone1().trim().equalsIgnoreCase(""))
                    tel_count++;
                if (!deliveryDetailData.getPhone2().trim().equalsIgnoreCase(""))
                    tel_count++;
                String[] tel = new String[tel_count];
                int tel_pointer = 0;
                if (!deliveryDetailData.getPhone1().trim().equalsIgnoreCase(""))
                    tel[tel_pointer++] = deliveryDetailData.getPhone1().trim();
                if (!deliveryDetailData.getPhone2().trim().equalsIgnoreCase(""))
                    tel[tel_pointer++] = deliveryDetailData.getPhone2().trim();

                if (tel_count > 1) {
                    final String delivery_tel[] = {tel[0], tel[1]};
                    new AlertDialog.Builder(deliveryDetailActivity).setTitle("번호를 선택하세요")
                            .setItems(delivery_tel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    String str = delivery_tel[i];
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

        menuButton = (Button)findViewById(R.id.delivery_detail_menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(deliveryDetailActivity, DeliveryMenuActivity.class);
                intent.putExtra("menu", ((DeliveryData) itemAndCommentDataList.get(0)).getMenu());
                intent.putExtra("phone1", ((DeliveryData)itemAndCommentDataList.get(0)).getPhone1());
                intent.putExtra("phone2", ((DeliveryData)itemAndCommentDataList.get(0)).getPhone2());
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.delivery_comments_recycler_view);
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

        likeImage = (ImageView)findViewById(R.id.delivery_detail_like_button_image);
        likeNumber = (TextView)findViewById(R.id.delivery_detail_like_button_number);
        callImage = (ImageView)findViewById(R.id.delivery_detail_call_button_image);
        menuImage = (ImageView)findViewById(R.id.delivery_detail_menu_button_image);


        contentsRefresh();
        setCommentAndReportButtons();
    }

    @Override
    public void downloadComments() {
        new ItemCommentAsyncTask(this, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_comments.php?page=" + String.valueOf(page) + "&report_only=" + reportOnly
                + "&item_id=" + deliveryDetailData.getItemId() + "&user_id=" + new MyCache(deliveryDetailActivity).getMyId()
                + "&default_code=" + new MyCache(deliveryDetailActivity).getDefaultCode()
                + "&content_id=" + new MyCache(deliveryDetailActivity).getContentId()
                + "&content_type=" + new MyCache(deliveryDetailActivity).getContentType());
    }

    @Override
    public void setPageContents() {
        deliveryDetailData = (DeliveryData)itemData;
        favoriteButton.setBackgroundResource(deliveryDetailData.getMyFavorite() == 1 ? R.drawable.item_detail_favorite_on : R.drawable.item_detail_favorite_off);
        likeImage.setImageResource(deliveryDetailData.getMyLike() == 1 ? R.drawable.item_detail_like_on : R.drawable.item_detail_like_off);
        likeNumber.setText("×" + deliveryDetailData.getLikes());
        callImage.setAlpha(deliveryDetailData.getPhone1().trim().equalsIgnoreCase("") && deliveryDetailData.getPhone2().trim().equalsIgnoreCase("") ?
                0.2f : 1f);
        menuImage.setAlpha(deliveryDetailData.getMenu().trim().equalsIgnoreCase("") ?
                0.2f : 1f);
    }

    @Override
    public void updateAnItem(int position) {
        if (position == 0) {
            deliveryDetailData = (DeliveryData) itemAndCommentDataList.get(0);
            setPageContents();
            return;
        }
        adapter.notifyItemChanged(position);
    }

    @Override
    public void showDownloadedList() {
        progressDialog.dismiss();

        if (page == 0) {
            adapter = new DeliveryDetailAdapter(deliveryDetailActivity, this, itemAndCommentDataList);
            recyclerView.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void afterItemModified(String deliveryId) {
        Intent intent = new Intent().putExtra("itemId", deliveryId);
        setResult(1, intent);
        finish();
    }

    public void afterDeliveryDeleted() {
        setResult(2);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
    }

}

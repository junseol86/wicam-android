package com.wicam.d_default_restaurant.detail_page;

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
import com.wicam.a_common_utils.ItemLikeAsyncTask;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.MapActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemCommentAsyncTask;
import com.wicam.a_common_utils.ItemFavoriteAsyncTask;
import com.wicam.d_default_restaurant.RestaurantCommentImageAsyncTask;
import com.wicam.d_default_restaurant.RestaurantData;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class RestaurantDetailActivity extends DetailActivityWithCommentAndReport {
    private RestaurantDetailAdapter adapter;
    private RestaurantData restaurantDetailData;
    private RestaurantDetailActivity restaurantDetailActivity;
    private boolean scrolling = false;

    private ImageButton favoriteButton;
    private Button likeButton, callButton, mapButton;
    private ImageView likeImage, callImage, mapImage;
    private TextView likeNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_restaurant_detail);

        restaurantDetailActivity = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오고 있습니다.");

        favoriteButton = (ImageButton)findViewById(R.id.restaurant_detail_favorite_button);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemFavoriteAsyncTask(restaurantDetailActivity, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id=" + restaurantDetailData.getItemId() +
                        "&user_id=" + new MyCache(restaurantDetailActivity).getMyId() + "&default_code=" + new MyCache(restaurantDetailActivity).getDefaultCode() +
                        "&content_id=" + new MyCache(restaurantDetailActivity).getContentId() + "&content_type=" + new MyCache(restaurantDetailActivity).getContentType());
            }
        });
        likeButton = (Button)findViewById(R.id.restaurant_detail_like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ItemLikeAsyncTask(restaurantDetailActivity, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_like_toggle.php?item_id=" + restaurantDetailData.getItemId() +
                        "&user_id=" + new MyCache(restaurantDetailActivity).getMyId() + "&default_code=" + new MyCache(restaurantDetailActivity).getDefaultCode() +
                        "&content_id=" + new MyCache(restaurantDetailActivity).getContentId() + "&content_type=" + new MyCache(restaurantDetailActivity).getContentType());
            }
        });

        callButton = (Button)findViewById(R.id.restaurant_detail_call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tel_count = 0;
                if (!restaurantDetailData.getPhone1().trim().equalsIgnoreCase(""))
                    tel_count++;
                if (!restaurantDetailData.getPhone2().trim().equalsIgnoreCase(""))
                    tel_count++;
                String[] tel = new String[tel_count];
                int tel_pointer = 0;
                if (!restaurantDetailData.getPhone1().trim().equalsIgnoreCase(""))
                    tel[tel_pointer++] = restaurantDetailData.getPhone1().trim();
                if (!restaurantDetailData.getPhone2().trim().equalsIgnoreCase(""))
                    tel[tel_pointer++] = restaurantDetailData.getPhone2().trim();

                if (tel_count > 1) {
                    final String restaurant_tel[] = {tel[0], tel[1]};
                    new AlertDialog.Builder(restaurantDetailActivity).setTitle("번호를 선택하세요")
                            .setItems(restaurant_tel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    String str = restaurant_tel[i];
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

        mapButton = (Button)findViewById(R.id.restaurant_detail_map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (restaurantDetailData.getLatitude().trim().equalsIgnoreCase("") || restaurantDetailData.getLongitude().trim().equalsIgnoreCase(""))
                    return;
                Singleton.create().setLatitude(Double.parseDouble(restaurantDetailData.getLatitude()));
                Singleton.create().setLongitude(Double.parseDouble(restaurantDetailData.getLongitude()));
                Singleton.create().setMarkerName(restaurantDetailData.getItemName());
                startActivity(new Intent(restaurantDetailActivity, MapActivity.class));
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.restaurant_comments_recycler_view);
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

        likeImage = (ImageView)findViewById(R.id.restaurant_detail_like_button_image);
        likeNumber = (TextView)findViewById(R.id.restaurant_detail_like_button_number);
        callImage = (ImageView)findViewById(R.id.item_detail_call_button_image);
        mapImage = (ImageView)findViewById(R.id.restaurant_detail_map_button_image);

        contentsRefresh();
        setCommentAndReportButtons();
    }

    @Override
    public void downloadComments() {
        new ItemCommentAsyncTask(this, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "item_comments.php?page=" + String.valueOf(page) + "&report_only=" + reportOnly
                + "&item_id=" + restaurantDetailData.getItemId() + "&user_id=" + new MyCache(restaurantDetailActivity).getMyId()
                + "&default_code=" + new MyCache(restaurantDetailActivity).getDefaultCode()
                + "&content_id=" + new MyCache(restaurantDetailActivity).getContentId()
                + "&content_type=" + new MyCache(restaurantDetailActivity).getContentType());
    }

    @Override
    public void setPageContents() {
        restaurantDetailData = (RestaurantData)itemData;
        favoriteButton.setBackgroundResource(restaurantDetailData.getMyFavorite() == 1 ? R.drawable.restaurant_detail_favorite_on : R.drawable.restaurant_detail_favorite_off);
        likeImage.setImageResource(restaurantDetailData.getMyLike() == 1 ? R.drawable.restaurant_detail_like_on : R.drawable.restaurant_detail_like_off);
        likeNumber.setText("×" + restaurantDetailData.getLikes());
        callImage.setAlpha(restaurantDetailData.getPhone1().trim().equalsIgnoreCase("") && restaurantDetailData.getPhone2().trim().equalsIgnoreCase("") ?
                0.2f : 1f);
        mapImage.setAlpha(restaurantDetailData.getLatitude().trim().equalsIgnoreCase("") || restaurantDetailData.getLongitude().trim().equalsIgnoreCase("") ?
                0.2f : 1f);
    }

    @Override
    public void updateAnItem(int position) {
        if (position == 0) {
            restaurantDetailData = (RestaurantData) itemAndCommentDataList.get(0);
            setPageContents();
            return;
        }
        adapter.notifyItemChanged(position);
    }

    @Override
    public void showDownloadedList() {
        progressDialog.dismiss();

        if (page == 0) {
            adapter = new RestaurantDetailAdapter(restaurantDetailActivity, this, itemAndCommentDataList);
            recyclerView.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();
    }

    @Override // 식당은 댓글작업 후 사진댓글의 갯수와 목록을 파악하여 화면적용해야 한다.
    protected  void afterCommentWorkWithImage() {
        new RestaurantCommentImageAsyncTask(this).execute(new Security().WEB_ADDRESS + "restaurant_photo_list.php?restaurant_id=" + Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemId());
    }

    @Override
    protected void afterItemModified(String restaurantId) {
        Intent intent = new Intent().putExtra("itemId", restaurantId);
        setResult(1, intent);
        finish();
    }

    // 사진 댓글들 파악한 뒤 화면적용 결과
    public void afterPhotoListDownloaded() {
        contentsRefresh();
    }

    public void afterRestaurantDeleted() {
        setResult(2);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(0);
        finish();
    }

}

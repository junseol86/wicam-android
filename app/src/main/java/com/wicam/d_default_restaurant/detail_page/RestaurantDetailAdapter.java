package com.wicam.d_default_restaurant.detail_page;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.CommentLikeAsyncTask;
import com.wicam.a_common_utils.ImageAsyncTask;
import com.wicam.a_common_utils.account_related.NickNameNeededTask;
import com.wicam.a_common_utils.account_related.comment_related.DeleteCommentAsyncTask;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentsAdapter;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.WebViewPage;
import com.wicam.a_common_utils.scroll_to_update.ViewholderWithComments;
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
import com.wicam.d_default_restaurant.RestaurantAddModifyActivity;
import com.wicam.d_default_restaurant.RestaurantData;
import com.wicam.d_default_restaurant.RestaurantDeleteAsyncTask;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class RestaurantDetailAdapter extends CommentsAdapter {

    private DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    private RestaurantDetailActivity restaurantDetailActivity;
    private RestaurantData restaurantDetailData;
    private int screenWidth;

    public RestaurantDetailAdapter(Activity activity, DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, ArrayList<ItemAndCommentData> itemAndCommentDataList) {
        this.activity = activity;
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.restaurantDetailActivity = (RestaurantDetailActivity) detailActivityWithCommentAndReport;
        this.itemAndCommentDataList = itemAndCommentDataList;

        Display display = restaurantDetailActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_restaurant_detail_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewholderWithComments holder, final int position) {
        super.onBindViewHolder(holder, position);

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        ((ViewHolder)holder).restaurantInfo.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        holder.itemView.findViewById(R.id.comment_ll).setVisibility(position != 0 ? View.VISIBLE : View.GONE);

        if (position == 0) { // 식당정보일 경우
            restaurantDetailData = (RestaurantData)itemAndCommentDataList.get(position);
            ((ViewHolder)holder).genre.setText(restaurantDetailData.getGenre());
            ((ViewHolder)holder).writerNickname.setText(restaurantDetailData.getModifierNickname());
            ((ViewHolder)holder).writeTime.setText(restaurantDetailData.getModifyTime());
            ((ViewHolder)holder).restaurantName.setText(restaurantDetailData.getItemName());

            String schoolList = "";
            for (int i = 0; i < restaurantDetailData.getSchoolNameList().size(); i++) {
                schoolList += restaurantDetailData.getSchoolNameList().get(i);
                if (i < restaurantDetailData.getSchoolNameList().size() - 1)
                    schoolList += ", ";
            }
            ((ViewHolder)holder).address.setText(restaurantDetailData.getAddress() + "\n주변학교: " + schoolList);
            ((ViewHolder)holder).description.setVisibility(restaurantDetailData.getDescription().trim().equals("") ? View.GONE : View.VISIBLE);
            ((ViewHolder)holder).description.setText(restaurantDetailData.getDescription());

            ((ViewHolder)holder).viewPager.getLayoutParams().height = (int)((double)screenWidth * 0.5);
            ((ViewHolder)holder).viewPagerContainter.setVisibility(restaurantDetailData.getPhotoList().size() > 0 ? View.VISIBLE : View.GONE);
            if (restaurantDetailData.getPhotoList().size() > 0) {
                ((ViewHolder)holder).viewPagerPage.setText("1/" + String.valueOf(restaurantDetailData.getPhotoList().size()));
                RestaurantPhotoVPAdapter restaurantPhotoVPAdapter = new RestaurantPhotoVPAdapter(restaurantDetailActivity, restaurantDetailData, ((ViewHolder)holder).viewPagerContainter);
                ((ViewHolder)holder).viewPager.setAdapter(restaurantPhotoVPAdapter);
            }
        }
        else {
            if (commentData.getHas_photo() == 1) {
                holder.commentPhoto.getLayoutParams().height = (int)((double)screenWidth * 0.4);
                holder.commentPhotoButton.getLayoutParams().height = (int)((double)screenWidth * 0.4);
                if (commentData.getImage() == null)
                    new ImageAsyncTask(this, new Security().RESTAURANT_IMAGE, commentData.getItem_comment_id(), position, holder.commentPhoto, commentData).execute();
                else {
                    holder.commentPhoto.setImageBitmap(commentData.getImage());
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return itemAndCommentDataList.size();
    }

    public class ViewHolder extends ViewholderWithComments {

        private LinearLayout restaurantInfo;
        private TextView genre, writerNickname, writeTime, restaurantName, address, description;
        private RelativeLayout viewPagerContainter;
        private ViewPager viewPager;
        private TextView viewPagerPage;
        private Button deleteModifyButton;

        public ViewHolder(View itemView) {
            super(itemView);
            restaurantInfo = (LinearLayout)itemView.findViewById(R.id.restaurant_detail_restaurant_information);
            genre = (TextView)itemView.findViewById(R.id.restaurant_detail_genre);
            writerNickname = (TextView)itemView.findViewById(R.id.restaurant_detail_writer_nickname);
            writeTime = (TextView)itemView.findViewById(R.id.restaurant_detail_write_time);
            restaurantName = (TextView)itemView.findViewById(R.id.restaurant_detail_restaurant_name);
            address = (TextView)itemView.findViewById(R.id.restaurant_detail_address);
            description = (TextView)itemView.findViewById(R.id.restaurant_detail_description);
            viewPagerContainter = (RelativeLayout)itemView.findViewById(R.id.restaurant_pics_view_pager_container);
            viewPager = (ViewPager)itemView.findViewById(R.id.restaurant_detail_view_pager);
            viewPagerPage = (TextView)itemView.findViewById(R.id.restaurant_detail_view_pager_page);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    viewPagerPage.setText(String.valueOf(position + 1) + "/" + String.valueOf(restaurantDetailData.getPhotoList().size()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            commentPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restaurantDetailActivity.startActivity(new Intent(restaurantDetailActivity, WebViewPage.class).putExtra("url_link",
                            new Security().WEB_ADDRESS + "image_show_fit.php?src=" + new Security().RESTAURANT_IMAGE +
                                    ((CommentData) itemAndCommentDataList.get(getPosition())).getItem_comment_id() + ".jpg"));
                }
            });

            urlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restaurantDetailActivity.startActivity(new Intent(detailActivityWithCommentAndReport, WebViewPage.class).putExtra("url_link", ((CommentData) itemAndCommentDataList.get(getPosition())).getUrl_link()));
                }
            });

            commentLike = (ImageButton)itemView.findViewById(R.id.comment_like_button);
            commentLikeNumber = (TextView)itemView.findViewById(R.id.comment_like_number);

            commentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CommentData)itemAndCommentDataList.get(getPosition())).getWriter_id().equalsIgnoreCase(new MyCache(restaurantDetailActivity).getMyId()))
                        return;
                    new CommentLikeAsyncTask(detailActivityWithCommentAndReport, itemAndCommentDataList, getPosition()).execute(new Security().WEB_ADDRESS
                            + "comment_like_toggle.php?user_id=" + new MyCache(restaurantDetailActivity).getMyId()
                            + "&default_code=" + new MyCache(restaurantDetailActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(restaurantDetailActivity).getContentId()
                            + "&content_type=" + new MyCache(restaurantDetailActivity).getContentType()
                            + "&comment_id=" + ((CommentData)itemAndCommentDataList.get(getPosition())).getItem_comment_id());
                }
            });

            deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(restaurantDetailActivity)
                            .setMessage("댓글을 삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new DeleteCommentAsyncTask(restaurantDetailActivity).execute(new Security().WEB_ADDRESS+ "delete_comment.php"
                                            + "?default_code=" + new MyCache(restaurantDetailActivity).getDefaultCode()
                                            + "&content_id=" + new MyCache(restaurantDetailActivity).getContentId()
                                            + "&content_type=" + new MyCache(restaurantDetailActivity).getContentType()
                                            + "&device_id=" + new MyCache(restaurantDetailActivity).getDeviceId()
                                            + "&authority_code=" + new MyCache(restaurantDetailActivity).getAuthority()
                                            + "&item_id=" + Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemId()
                                            + "&user_id=" + new MyCache(restaurantDetailActivity).getMyId()
                                            + "&writer_id=" + ((CommentData)itemAndCommentDataList.get(getPosition())).getWriter_id()
                                            + "&comment_id=" + ((CommentData)itemAndCommentDataList.get(getPosition())).getItem_comment_id()
                                    );
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }
            });


            deleteModifyButton = (Button)itemView.findViewById(R.id.restaurant_modify_delete_btn);
            deleteModifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (restaurantDetailData.getAuthority() == 1) {
                        new AlertDialog.Builder(restaurantDetailActivity)
                                .setMessage("식당정보를 수정 혹은 삭제하시겠습니까?")
                                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Singleton.create().setAddOrModify(1);
                                        Singleton.create().setRestaurantData((RestaurantData)itemAndCommentDataList.get(0));
                                        restaurantDetailActivity.startActivityForResult(new Intent(restaurantDetailActivity, RestaurantAddModifyActivity.class), 1);
                                    }
                                })
                                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AlertDialog.Builder(restaurantDetailActivity)
                                                .setMessage("식당정보를 정말로 삭제하시겠습니까?")
                                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        new RestaurantDeleteAsyncTask(restaurantDetailActivity).execute(new Security().WEB_ADDRESS + "delete_restaurant.php?user_id=" + new MyCache(restaurantDetailActivity).getMyId()
                                                        + "&device_id=" + new MyCache(restaurantDetailActivity).getDeviceId() + "&item_id=" + ((RestaurantData)itemAndCommentDataList.get(0)).getItemId()
                                                                + "&default_code=" + new MyCache(restaurantDetailActivity).getDefaultCode() + "&content_id=" + new MyCache(restaurantDetailActivity).getContentId()
                                                        );
                                                    }
                                                })
                                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                    }
                                                })
                                                .show();
                                    }
                                })
                                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    }
                    else {
                        new AlertDialog.Builder(restaurantDetailActivity)
                                .setMessage("다른 사용자에 의해 작성된 정보입니다.  정보의 수정·삭제 권한을 신청하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new NickNameNeededTask(restaurantDetailActivity, 3);
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    }
                }
            });
        }


    }
}

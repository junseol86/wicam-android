package com.wicam.d_default_delivery.detail_page;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.CommentLikeAsyncTask;
import com.wicam.a_common_utils.ImageAsyncTask;
import com.wicam.a_common_utils.WebViewPage;
import com.wicam.a_common_utils.account_related.NickNameNeededTask;
import com.wicam.a_common_utils.account_related.comment_related.DeleteCommentAsyncTask;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentsAdapter;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.scroll_to_update.ViewholderWithComments;
import com.wicam.d_default_delivery.DeliveryAddModifyActivity;
import com.wicam.d_default_delivery.DeliveryData;
import com.wicam.d_default_delivery.DeliveryDeleteAsyncTask;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class DeliveryDetailAdapter extends CommentsAdapter {

    private DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    private DeliveryDetailActivity deliveryDetailActivity;
    private DeliveryData deliveryDetailData;
    private int screenWidth;

    public DeliveryDetailAdapter(Activity activity, DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, ArrayList<ItemAndCommentData> itemAndCommentDataList) {
        this.activity = activity;
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.deliveryDetailActivity = (DeliveryDetailActivity) detailActivityWithCommentAndReport;
        this.itemAndCommentDataList = itemAndCommentDataList;

        Display display = deliveryDetailActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_delivery_detail_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewholderWithComments holder, final int position) {
        super.onBindViewHolder(holder, position);

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        ((ViewHolder)holder).deliveryInfo.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        ((ViewHolder)holder).itemView.findViewById(R.id.comment_ll).setVisibility(position != 0 ? View.VISIBLE : View.GONE);

        if (position == 0) { // 업체정보일 경우
            deliveryDetailData = (DeliveryData)itemAndCommentDataList.get(position);
            ((ViewHolder)holder).writerNickname.setText(deliveryDetailData.getModifierNickname());
            ((ViewHolder)holder).writeTime.setText(deliveryDetailData.getModifyTime());
            ((ViewHolder)holder).deliveryName.setText(deliveryDetailData.getItemName());

            String schoolList = "";
            for (int i = 0; i < deliveryDetailData.getSchoolNameList().size(); i++) {
                schoolList += deliveryDetailData.getSchoolNameList().get(i);
                if (i < deliveryDetailData.getSchoolNameList().size() - 1)
                    schoolList += ", ";
            }
            ((ViewHolder)holder).condition.setVisibility(deliveryDetailData.getDeliveryCondition().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            ((ViewHolder)holder).condition.setText(deliveryDetailData.getDeliveryCondition());
            ((ViewHolder)holder).schools.setText("배달학교: " + schoolList);
            ((ViewHolder)holder).description.setVisibility(deliveryDetailData.getDescription().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            ((ViewHolder)holder).description.setText(deliveryDetailData.getDescription());

            ((ViewHolder)holder).leafletArea.setVisibility(deliveryDetailData.getHasPhoto() == 0 ? View.GONE : View.VISIBLE);
            if (deliveryDetailData.getHasPhoto() == 1) {
                ((ViewHolder)holder).leaflet.getLayoutParams().height = (int)((double)screenWidth * 0.5);
                ((ViewHolder)holder).seeLeafletButton.getLayoutParams().height = (int)((double)screenWidth * 0.5);
                if (deliveryDetailData.getImage() == null)
                    new ImageAsyncTask(this, new Security().DELIVERY_IMAGE, "item_" + deliveryDetailData.getItemId(), position, ((ViewHolder) holder).leaflet, deliveryDetailData).execute();
                else {
                    ((ViewHolder) holder).leaflet.setImageBitmap(deliveryDetailData.getImage());
                }
            }

        }
        else {
            holder.commentPhoto.getLayoutParams().height = (int)((double)screenWidth * 0.4);
            holder.commentPhotoButton.getLayoutParams().height = (int)((double)screenWidth * 0.4);
            if (commentData.getHas_photo() == 1) {
                if (commentData.getImage() == null)
                    new ImageAsyncTask(this, new Security().DELIVERY_IMAGE, commentData.getItem_comment_id(), position, holder.commentPhoto, commentData).execute();
                else
                    holder.commentPhoto.setImageBitmap(commentData.getImage());
            }
        }


    }

    @Override
    public int getItemCount() {
        return itemAndCommentDataList.size();
    }

    public class ViewHolder extends ViewholderWithComments {

        private LinearLayout deliveryInfo;
        private TextView writerNickname, writeTime, deliveryName, condition, schools, description;
        private Button deleteModifyButton, seeLeafletButton;
        private RelativeLayout leafletArea;
        private ImageView leaflet;

        public ViewHolder(final View itemView) {
            super(itemView);
            deliveryInfo = (LinearLayout)itemView.findViewById(R.id.delivery_detail_delivery_information);
            writerNickname = (TextView)itemView.findViewById(R.id.delivery_detail_writer_nickname);
            writeTime = (TextView)itemView.findViewById(R.id.delivery_detail_write_time);
            deliveryName = (TextView)itemView.findViewById(R.id.delivery_detail_delivery_name);
            condition = (TextView)itemView.findViewById(R.id.delivery_detail_condition);
            schools = (TextView)itemView.findViewById(R.id.delivery_detail_schools);
            description = (TextView)itemView.findViewById(R.id.delivery_detail_description);

            commentPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryDetailActivity.startActivity(new Intent(deliveryDetailActivity, WebViewPage.class).putExtra("url_link",
                            new Security().WEB_ADDRESS + "image_show_fit.php?src=" + new Security().DELIVERY_IMAGE +
                                    ((CommentData) itemAndCommentDataList.get(getPosition())).getItem_comment_id() + ".jpg"));
                }
            });

            urlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deliveryDetailActivity.startActivity(new Intent(detailActivityWithCommentAndReport, WebViewPage.class).putExtra("url_link", ((CommentData) itemAndCommentDataList.get(getPosition())).getUrl_link()));
                }
            });

            commentLike = (ImageButton)itemView.findViewById(R.id.comment_like_button);
            commentLikeNumber = (TextView)itemView.findViewById(R.id.comment_like_number);

            commentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CommentData)itemAndCommentDataList.get(getPosition())).getWriter_id().equalsIgnoreCase(new MyCache(deliveryDetailActivity).getMyId()))
                        return;
                    new CommentLikeAsyncTask(detailActivityWithCommentAndReport, itemAndCommentDataList, getPosition()).execute(new Security().WEB_ADDRESS
                            + "comment_like_toggle.php?user_id=" + new MyCache(deliveryDetailActivity).getMyId()
                            + "&default_code=" + new MyCache(deliveryDetailActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(deliveryDetailActivity).getContentId()
                            + "&content_type=" + new MyCache(deliveryDetailActivity).getContentType()
                            + "&comment_id=" + ((CommentData) itemAndCommentDataList.get(getPosition())).getItem_comment_id());
                }
            });

            deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(deliveryDetailActivity)
                            .setMessage("댓글을 삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new DeleteCommentAsyncTask(deliveryDetailActivity).execute(new Security().WEB_ADDRESS+ "delete_comment.php"
                                            + "?default_code=" + new MyCache(deliveryDetailActivity).getDefaultCode()
                                            + "&content_id=" + new MyCache(deliveryDetailActivity).getContentId()
                                            + "&content_type=" + new MyCache(deliveryDetailActivity).getContentType()
                                            + "&device_id=" + new MyCache(deliveryDetailActivity).getDeviceId()
                                            + "&authority_code=" + new MyCache(deliveryDetailActivity).getAuthority()
                                            + "&item_id=" + Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemId()
                                            + "&user_id=" + new MyCache(deliveryDetailActivity).getMyId()
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

            leafletArea = (RelativeLayout)itemView.findViewById(R.id.delivery_leaflet_rl);
            seeLeafletButton = (Button)itemView.findViewById(R.id.delivery_leaflet_button);
            seeLeafletButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ((ImageView)itemView.findViewById(R.id.delivery_leaflet_image)).setImageResource(R.drawable.image_magnify_over);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            ((ImageView)itemView.findViewById(R.id.delivery_leaflet_image)).setImageResource(R.drawable.image_magnify);
                            break;
                        case MotionEvent.ACTION_UP:
                            ((ImageView)itemView.findViewById(R.id.delivery_leaflet_image)).setImageResource(R.drawable.image_magnify);
                            deliveryDetailActivity.startActivity(new Intent(deliveryDetailActivity, WebViewPage.class).putExtra("url_link",
                                    new Security().WEB_ADDRESS + "image_show_fit.php?src=" + new Security().DELIVERY_IMAGE + "item_" +
                                            ((ItemData)itemAndCommentDataList.get(0)).getItemId() + ".jpg"));
                            break;
                    }
                    return false;
                }
            });

            leaflet = (ImageView)itemView.findViewById(R.id.delivery_leaflet);

            deleteModifyButton = (Button)itemView.findViewById(R.id.delivery_modify_delete_btn);
            deleteModifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deliveryDetailData.getAuthority() == 1) {
                        new AlertDialog.Builder(deliveryDetailActivity)
                                .setMessage("식당정보를 수정 혹은 삭제하시겠습니까?")
                                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Singleton.create().setAddOrModify(1);
                                        Singleton.create().setDeliveryData((DeliveryData)itemAndCommentDataList.get(0));
                                        deliveryDetailActivity.startActivityForResult(new Intent(deliveryDetailActivity, DeliveryAddModifyActivity.class), 1);
                                    }
                                })
                                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AlertDialog.Builder(deliveryDetailActivity)
                                                .setMessage("식당정보를 정말로 삭제하시겠습니까?")
                                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        new DeliveryDeleteAsyncTask(deliveryDetailActivity).execute(new Security().WEB_ADDRESS + "delete_delivery.php?user_id=" + new MyCache(deliveryDetailActivity).getMyId()
                                                        + "&device_id=" + new MyCache(deliveryDetailActivity).getDeviceId() + "&item_id=" + ((DeliveryData)itemAndCommentDataList.get(0)).getItemId()
                                                                + "&default_code=" + new MyCache(deliveryDetailActivity).getDefaultCode() + "&content_id=" + new MyCache(deliveryDetailActivity).getContentId()
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
                        new AlertDialog.Builder(deliveryDetailActivity)
                                .setMessage("다른 사용자에 의해 작성된 정보입니다.  정보의 수정·삭제 권한을 신청하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new NickNameNeededTask(deliveryDetailActivity, 3);
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

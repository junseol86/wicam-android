package com.wicam.d_default_custom.detail_page;

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
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.CommentsAdapter;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.scroll_to_update.ViewholderWithComments;
import com.wicam.d_default_custom.CustomAddModifyActivity;
import com.wicam.d_default_custom.CustomData;
import com.wicam.d_default_custom.CustomDeleteAsyncTask;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class CustomDetailAdapter extends CommentsAdapter {

    private DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    private CustomDetailActivity customDetailActivity;
    private CustomData customDetailData;
    private int screenWidth;

    public CustomDetailAdapter(Activity activity, DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, ArrayList<ItemAndCommentData> itemAndCommentDataList) {
        this.activity = activity;
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.customDetailActivity = (CustomDetailActivity) detailActivityWithCommentAndReport;
        this.itemAndCommentDataList = itemAndCommentDataList;

        Display display = customDetailActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_custom_detail_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewholderWithComments holder, final int position) {
        super.onBindViewHolder(holder, position);

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        ((ViewHolder)holder).customInfo.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        ((ViewHolder)holder).itemView.findViewById(R.id.comment_ll).setVisibility(position != 0 ? View.VISIBLE : View.GONE);

        if (position == 0) { // 업체정보일 경우
            customDetailData = (CustomData)itemAndCommentDataList.get(position);
            ((ViewHolder)holder).writerNickname.setText(customDetailData.getModifierNickname());
            ((ViewHolder)holder).writeTime.setText(customDetailData.getModifyTime());
            ((ViewHolder)holder).customName.setText(customDetailData.getItemName());
            ((ViewHolder)holder).urlLink.setVisibility(customDetailData.getUrlLink().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            ((ViewHolder)holder).urlLink.setText(customDetailData.getUrlLink());

            ((ViewHolder)holder).description.setVisibility(customDetailData.getDescription().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            ((ViewHolder)holder).description.setText(customDetailData.getDescription());

            ((ViewHolder)holder).itemImageArea.setVisibility(customDetailData.getHasPhoto() == 0 ? View.GONE : View.VISIBLE);
            if (customDetailData.getHasPhoto() == 1) {
                ((ViewHolder)holder).itemImage.getLayoutParams().height = (int)((double)screenWidth * 0.4);
                ((ViewHolder)holder).seeImageButton.getLayoutParams().height = (int)((double)screenWidth * 0.4);
                if (customDetailData.getImage() == null)
                    new ImageAsyncTask(this, new Security().CUSTOM_IMAGE, "item_" + customDetailData.getItemId(), position, ((ViewHolder) holder).itemImage, customDetailData).execute();
                else {
                    ((ViewHolder) holder).itemImage.setImageBitmap(customDetailData.getImage());
                }
            }

        }
        else {
            holder.commentPhoto.getLayoutParams().height = (int)((double)screenWidth * 0.4);
            holder.commentPhotoButton.getLayoutParams().height = (int)((double)screenWidth * 0.4);
            if (commentData.getHas_photo() == 1) {
                if (commentData.getImage() == null)
                    new ImageAsyncTask(this, new Security().CUSTOM_IMAGE, commentData.getItem_comment_id(), position, holder.commentPhoto, commentData).execute();
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

        private LinearLayout customInfo;
        private TextView writerNickname, writeTime, customName, description, urlLink;
        private Button deleteModifyButton, seeImageButton;
        private RelativeLayout itemImageArea;
        private ImageView itemImage;

        public ViewHolder(final View itemView) {
            super(itemView);
            customInfo = (LinearLayout)itemView.findViewById(R.id.custom_detail_custom_information);
            writerNickname = (TextView)itemView.findViewById(R.id.custom_detail_writer_nickname);
            writeTime = (TextView)itemView.findViewById(R.id.custom_detail_write_time);
            customName = (TextView)itemView.findViewById(R.id.custom_detail_custom_name);
            description = (TextView)itemView.findViewById(R.id.custom_detail_description);
            urlLink = (TextView)itemView.findViewById(R.id.custom_detail_url_link);
            urlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDetailActivity.startActivity(new Intent(detailActivityWithCommentAndReport, WebViewPage.class).putExtra("url_link", ((CustomData) itemAndCommentDataList.get(getPosition())).getUrlLink()));
                }
            });

            commentPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDetailActivity.startActivity(new Intent(customDetailActivity, WebViewPage.class).putExtra("url_link",
                            new Security().WEB_ADDRESS + "image_show_fit.php?src=" + new Security().CUSTOM_IMAGE +
                                    ((CommentData) itemAndCommentDataList.get(getPosition())).getItem_comment_id() + ".jpg"));
                }
            });

            urlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDetailActivity.startActivity(new Intent(detailActivityWithCommentAndReport, WebViewPage.class).putExtra("url_link", ((CommentData) itemAndCommentDataList.get(getPosition())).getUrl_link()));
                }
            });

            commentLike = (ImageButton)itemView.findViewById(R.id.comment_like_button);
            commentLikeNumber = (TextView)itemView.findViewById(R.id.comment_like_number);

            commentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CommentData)itemAndCommentDataList.get(getPosition())).getWriter_id().equalsIgnoreCase(new MyCache(customDetailActivity).getMyId()))
                        return;
                    new CommentLikeAsyncTask(detailActivityWithCommentAndReport, itemAndCommentDataList, getPosition()).execute(new Security().WEB_ADDRESS
                            + "comment_like_toggle.php?user_id=" + new MyCache(customDetailActivity).getMyId()
                            + "&default_code=" + new MyCache(customDetailActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(customDetailActivity).getContentId()
                            + "&content_type=" + new MyCache(customDetailActivity).getContentType()
                            + "&comment_id=" + ((CommentData) itemAndCommentDataList.get(getPosition())).getItem_comment_id());
                }
            });

            deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(customDetailActivity)
                            .setMessage("댓글을 삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new DeleteCommentAsyncTask(customDetailActivity).execute(new Security().WEB_ADDRESS+ "delete_comment.php"
                                            + "?default_code=" + new MyCache(customDetailActivity).getDefaultCode()
                                            + "&content_id=" + new MyCache(customDetailActivity).getContentId()
                                            + "&content_type=" + new MyCache(customDetailActivity).getContentType()
                                            + "&device_id=" + new MyCache(customDetailActivity).getDeviceId()
                                            + "&authority_code=" + new MyCache(customDetailActivity).getAuthority()
                                            + "&item_id=" + Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemId()
                                            + "&user_id=" + new MyCache(customDetailActivity).getMyId()
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

            itemImageArea = (RelativeLayout)itemView.findViewById(R.id.custom_image_rl);
            seeImageButton = (Button)itemView.findViewById(R.id.custom_image_button);
            seeImageButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ((ImageView)itemView.findViewById(R.id.item_magnify_image)).setImageResource(R.drawable.image_magnify_over);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            ((ImageView)itemView.findViewById(R.id.item_magnify_image)).setImageResource(R.drawable.image_magnify);
                            break;
                        case MotionEvent.ACTION_UP:
                            ((ImageView)itemView.findViewById(R.id.item_magnify_image)).setImageResource(R.drawable.image_magnify);
                            customDetailActivity.startActivity(new Intent(customDetailActivity, WebViewPage.class).putExtra("url_link",
                                    new Security().WEB_ADDRESS + "image_show_fit.php?src=" + new Security().CUSTOM_IMAGE + "item_" +
                                            ((ItemData)itemAndCommentDataList.get(0)).getItemId() + ".jpg"));
                            break;
                    }
                    return false;
                }
            });

            itemImage = (ImageView)itemView.findViewById(R.id.custom_image);

            deleteModifyButton = (Button)itemView.findViewById(R.id.custom_modify_delete_btn);
            deleteModifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (customDetailData.getAuthority() == 1) {
                        new AlertDialog.Builder(customDetailActivity)
                                .setMessage("정보를 수정 혹은 삭제하시겠습니까?")
                                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Singleton.create().setAddOrModify(1);
                                        Singleton.create().setCustomData((CustomData) itemAndCommentDataList.get(0));
                                        customDetailActivity.startActivityForResult(new Intent(customDetailActivity, CustomAddModifyActivity.class), 1);
                                    }
                                })
                                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AlertDialog.Builder(customDetailActivity)
                                                .setMessage("정보를 정말로 삭제하시겠습니까?")
                                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        new CustomDeleteAsyncTask(customDetailActivity).execute(new Security().WEB_ADDRESS + "delete_custom.php?user_id=" + new MyCache(customDetailActivity).getMyId()
                                                        + "&device_id=" + new MyCache(customDetailActivity).getDeviceId() + "&item_id=" + ((CustomData)itemAndCommentDataList.get(0)).getItemId()
                                                                + "&default_code=" + new MyCache(customDetailActivity).getDefaultCode() + "&content_id=" + new MyCache(customDetailActivity).getContentId()
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
                        new AlertDialog.Builder(customDetailActivity)
                                .setMessage("다른 사용자에 의해 작성된 정보입니다.  정보의 수정·삭제 권한을 신청하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new NickNameNeededTask(customDetailActivity, 3);
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

package com.wicam.d_default_phonebook.detail_page;

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
import com.wicam.d_default_phonebook.PhonebookAddModifyActivity;
import com.wicam.d_default_phonebook.PhonebookData;
import com.wicam.d_default_phonebook.PhonebookDeleteAsyncTask;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class PhonebookDetailAdapter extends CommentsAdapter {

    private DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    private PhonebookDetailActivity phonebookDetailActivity;
    private PhonebookData phonebookDetailData;
    private int screenWidth;

    public PhonebookDetailAdapter(Activity activity, DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, ArrayList<ItemAndCommentData> itemAndCommentDataList) {
        this.activity = activity;
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.phonebookDetailActivity = (PhonebookDetailActivity) detailActivityWithCommentAndReport;
        this.itemAndCommentDataList = itemAndCommentDataList;

        Display display = phonebookDetailActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_phonebook_detail_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewholderWithComments holder, final int position) {
        super.onBindViewHolder(holder, position);

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        ((ViewHolder)holder).phonebookInfo.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        ((ViewHolder)holder).itemView.findViewById(R.id.comment_ll).setVisibility(position != 0 ? View.VISIBLE : View.GONE);

        if (position == 0) { // 업체정보일 경우
            phonebookDetailData = (PhonebookData)itemAndCommentDataList.get(position);
            ((ViewHolder)holder).writerNickname.setText(phonebookDetailData.getModifierNickname());
            ((ViewHolder)holder).writeTime.setText(phonebookDetailData.getModifyTime());
            ((ViewHolder)holder).phonebookName.setText(phonebookDetailData.getItemName());
            ((ViewHolder)holder).kind.setText(phonebookDetailData.getKind());
            ((ViewHolder)holder).urlLink.setVisibility(phonebookDetailData.getUrlLink().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            ((ViewHolder)holder).urlLink.setText(phonebookDetailData.getUrlLink());

            String schoolList = "";
            for (int i = 0; i < phonebookDetailData.getSchoolNameList().size(); i++) {
                schoolList += phonebookDetailData.getSchoolNameList().get(i);
                if (i < phonebookDetailData.getSchoolNameList().size() - 1)
                    schoolList += ", ";
            }
            ((ViewHolder)holder).schools.setText("소속학교: " + schoolList);
            ((ViewHolder)holder).description.setVisibility(phonebookDetailData.getDescription().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            ((ViewHolder)holder).description.setText(phonebookDetailData.getDescription());

            ((ViewHolder)holder).itemImageArea.setVisibility(phonebookDetailData.getHasPhoto() == 0 ? View.GONE : View.VISIBLE);
            if (phonebookDetailData.getHasPhoto() == 1) {
                ((ViewHolder)holder).itemImage.getLayoutParams().height = (int)((double)screenWidth * 0.75);
                ((ViewHolder)holder).seeImageButton.getLayoutParams().height = (int)((double)screenWidth * 0.75);
                if (phonebookDetailData.getImage() == null)
                    new ImageAsyncTask(this, new Security().PHONEBOOK_IMAGE, "item_" + phonebookDetailData.getItemId(), position, ((ViewHolder) holder).itemImage, phonebookDetailData).execute();
                else {
                    ((ViewHolder) holder).itemImage.setImageBitmap(phonebookDetailData.getImage());
                }
            }

        }
        else {
            holder.commentPhoto.getLayoutParams().height = (int)((double)screenWidth * 0.4);
            holder.commentPhotoButton.getLayoutParams().height = (int)((double)screenWidth * 0.4);
            if (commentData.getHas_photo() == 1) {
                if (commentData.getImage() == null)
                    new ImageAsyncTask(this, new Security().PHONEBOOK_IMAGE, commentData.getItem_comment_id(), position, holder.commentPhoto, commentData).execute();
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

        private LinearLayout phonebookInfo;
        private TextView writerNickname, writeTime, phonebookName, kind, description, urlLink, schools;
        private Button deleteModifyButton, seeImageButton;
        private RelativeLayout itemImageArea;
        private ImageView itemImage;

        public ViewHolder(final View itemView) {
            super(itemView);
            phonebookInfo = (LinearLayout)itemView.findViewById(R.id.phonebook_detail_phonebook_information);
            writerNickname = (TextView)itemView.findViewById(R.id.phonebook_detail_writer_nickname);
            writeTime = (TextView)itemView.findViewById(R.id.phonebook_detail_write_time);
            phonebookName = (TextView)itemView.findViewById(R.id.phonebook_detail_phonebook_name);
            kind = (TextView)itemView.findViewById(R.id.phonebook_detail_kind);
            schools = (TextView)itemView.findViewById(R.id.phonebook_detail_schools);
            description = (TextView)itemView.findViewById(R.id.phonebook_detail_description);
            urlLink = (TextView)itemView.findViewById(R.id.phonebook_detail_url_link);
            urlLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonebookDetailActivity.startActivity(new Intent(detailActivityWithCommentAndReport, WebViewPage.class).putExtra("url_link", ((PhonebookData) itemAndCommentDataList.get(getPosition())).getUrlLink()));
                }
            });

            commentPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonebookDetailActivity.startActivity(new Intent(phonebookDetailActivity, WebViewPage.class).putExtra("url_link",
                            new Security().WEB_ADDRESS + "image_show_fit.php?src=" + new Security().PHONEBOOK_IMAGE +
                                    ((CommentData) itemAndCommentDataList.get(getPosition())).getItem_comment_id() + ".jpg"));
                }
            });

            urlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phonebookDetailActivity.startActivity(new Intent(detailActivityWithCommentAndReport, WebViewPage.class).putExtra("url_link", ((CommentData) itemAndCommentDataList.get(getPosition())).getUrl_link()));
                }
            });

            commentLike = (ImageButton)itemView.findViewById(R.id.comment_like_button);
            commentLikeNumber = (TextView)itemView.findViewById(R.id.comment_like_number);

            commentLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CommentData)itemAndCommentDataList.get(getPosition())).getWriter_id().equalsIgnoreCase(new MyCache(phonebookDetailActivity).getMyId()))
                        return;
                    new CommentLikeAsyncTask(detailActivityWithCommentAndReport, itemAndCommentDataList, getPosition()).execute(new Security().WEB_ADDRESS
                            + "comment_like_toggle.php?user_id=" + new MyCache(phonebookDetailActivity).getMyId()
                            + "&default_code=" + new MyCache(phonebookDetailActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(phonebookDetailActivity).getContentId()
                            + "&content_type=" + new MyCache(phonebookDetailActivity).getContentType()
                            + "&comment_id=" + ((CommentData) itemAndCommentDataList.get(getPosition())).getItem_comment_id());
                }
            });

            deleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(phonebookDetailActivity)
                            .setMessage("댓글을 삭제하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new DeleteCommentAsyncTask(phonebookDetailActivity).execute(new Security().WEB_ADDRESS+ "delete_comment.php"
                                            + "?default_code=" + new MyCache(phonebookDetailActivity).getDefaultCode()
                                            + "&content_id=" + new MyCache(phonebookDetailActivity).getContentId()
                                            + "&content_type=" + new MyCache(phonebookDetailActivity).getContentType()
                                            + "&device_id=" + new MyCache(phonebookDetailActivity).getDeviceId()
                                            + "&authority_code=" + new MyCache(phonebookDetailActivity).getAuthority()
                                            + "&item_id=" + Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemId()
                                            + "&user_id=" + new MyCache(phonebookDetailActivity).getMyId()
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

            itemImageArea = (RelativeLayout)itemView.findViewById(R.id.phonebook_image_rl);
            seeImageButton = (Button)itemView.findViewById(R.id.phonebook_image_button);
            seeImageButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            break;
                        case MotionEvent.ACTION_UP:
                            phonebookDetailActivity.startActivity(new Intent(phonebookDetailActivity, WebViewPage.class).putExtra("url_link",
                                    new Security().WEB_ADDRESS + "image_show_fit.php?src=" + new Security().PHONEBOOK_IMAGE + "item_" +
                                            ((ItemData)itemAndCommentDataList.get(0)).getItemId() + ".jpg"));
                            break;
                    }
                    return false;
                }
            });

            itemImage = (ImageView)itemView.findViewById(R.id.phonebook_image);

            deleteModifyButton = (Button)itemView.findViewById(R.id.phonebook_modify_delete_btn);
            deleteModifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (phonebookDetailData.getAuthority() == 1) {
                        new AlertDialog.Builder(phonebookDetailActivity)
                                .setMessage("정보를 수정 혹은 삭제하시겠습니까?")
                                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Singleton.create().setAddOrModify(1);
                                        Singleton.create().setPhonebookData((PhonebookData)itemAndCommentDataList.get(0));
                                        phonebookDetailActivity.startActivityForResult(new Intent(phonebookDetailActivity, PhonebookAddModifyActivity.class), 1);
                                    }
                                })
                                .setNegativeButton("삭제", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new AlertDialog.Builder(phonebookDetailActivity)
                                                .setMessage("정보를 정말로 삭제하시겠습니까?")
                                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        new PhonebookDeleteAsyncTask(phonebookDetailActivity).execute(new Security().WEB_ADDRESS + "delete_phonebook.php?user_id=" + new MyCache(phonebookDetailActivity).getMyId()
                                                        + "&device_id=" + new MyCache(phonebookDetailActivity).getDeviceId() + "&item_id=" + ((PhonebookData)itemAndCommentDataList.get(0)).getItemId()
                                                                + "&default_code=" + new MyCache(phonebookDetailActivity).getDefaultCode() + "&content_id=" + new MyCache(phonebookDetailActivity).getContentId()
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
                        new AlertDialog.Builder(phonebookDetailActivity)
                                .setMessage("다른 사용자에 의해 작성된 정보입니다.  정보의 수정·삭제 권한을 신청하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new NickNameNeededTask(phonebookDetailActivity, 3);
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

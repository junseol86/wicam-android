package com.wicam.a_common_utils.scroll_to_update;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wicam.R;

/**
 * Created by Hyeonmin on 2015-07-22.
 */
public class ViewholderWithComments extends RecyclerView.ViewHolder {

    public CardView commentCardView;
    public TextView commenter, commentTime, comment;
    public RelativeLayout commentPhotoRl;
    public ImageView commentPhoto;
    public Button commentPhotoButton, urlBtn;
    public ImageButton deleteComment;
    public ImageButton commentLike;
    public TextView commentLikeNumber;
    
    public ViewholderWithComments(View itemView) {
        super(itemView);
        commentCardView = (CardView)itemView.findViewById(R.id.comment_card_view);
        commenter = (TextView)itemView.findViewById(R.id.commenter_nickname);
        commentTime = (TextView)itemView.findViewById(R.id.comment_time);
        comment = (TextView)itemView.findViewById(R.id.comment_text);
        commentPhotoRl = (RelativeLayout)itemView.findViewById(R.id.comment_photo_rl);
        commentPhoto = (ImageView)itemView.findViewById(R.id.comment_photo);
        commentPhotoButton = (Button)itemView.findViewById(R.id.comment_photo_button);


        urlBtn = (Button)itemView.findViewById(R.id.comment_url_btn);

        deleteComment = (ImageButton)itemView.findViewById(R.id.delete_comment);
    }
}

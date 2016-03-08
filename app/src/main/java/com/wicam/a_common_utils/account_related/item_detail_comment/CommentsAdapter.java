package com.wicam.a_common_utils.account_related.item_detail_comment;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.scroll_to_update.ViewholderWithComments;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-23.
 */
public class CommentsAdapter extends RecyclerView.Adapter<ViewholderWithComments> {
    // 범용 댓글을 포함하는 컨텐츠

    protected CommentData commentData;
    protected ArrayList<ItemAndCommentData> itemAndCommentDataList;
    protected Activity activity;

    @Override
    public ViewholderWithComments onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewholderWithComments holder, int position) {
        if (position > 0) { //댓글일 경우
            commentData = (CommentData)itemAndCommentDataList.get(position);

            ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(commentData.isToShowLoading() && position == itemAndCommentDataList.size() - 1 ? View.VISIBLE : View.GONE);
            holder.commentCardView.setVisibility(commentData.isEmpty() ? View.GONE : View.VISIBLE);
            ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 댓글이 없습니다.");
            ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!commentData.isEmpty() ? View.GONE : View.VISIBLE);
            ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == itemAndCommentDataList.size() - 1 ? View.VISIBLE : View.GONE);
            ((RelativeLayout)holder.itemView.findViewById(R.id.is_report)).setVisibility(commentData.getReport() == 1 ? View.VISIBLE : View.GONE);

            holder.commenter.setText(commentData.getWriter_nickname());
            holder.commentTime.setText(commentData.getWrite_time());
            holder.comment.setVisibility(commentData.getComment().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            holder.comment.setText(commentData.getComment());
            ((RelativeLayout)holder.itemView.findViewById(R.id.comment_photo_rl)).setVisibility(commentData.getHas_photo() == 0 ? View.GONE : View.VISIBLE);
            holder.urlBtn.setVisibility(!commentData.getUrl_link().trim().equalsIgnoreCase("") ? View.VISIBLE : View.GONE);
            if (!commentData.getUrl_link().trim().equalsIgnoreCase("")) {
                holder.urlBtn.setText(commentData.getUrl_link().replace("http://", ""));
                holder.urlBtn.setPaintFlags(holder.urlBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            }

            holder.deleteComment.setVisibility(new MyCache(activity).getMyId().equalsIgnoreCase(commentData.getWriter_id()) || !new MyCache(activity).getAuthority().equalsIgnoreCase("") ? View.VISIBLE : View.GONE);

            holder.commentLike.setBackgroundResource(commentData.getMy_like() == 0 ? R.drawable.comment_like_off : R.drawable.comment_like_on);
            holder.commentLike.setAlpha(commentData.getWriter_id().equalsIgnoreCase(new MyCache(activity).getMyId()) ? 0.3f : 1.0f);
            holder.commentLikeNumber.setText("×" + (commentData.getLikes()));
        }

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

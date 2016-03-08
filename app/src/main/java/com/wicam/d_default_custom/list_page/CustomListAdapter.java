package com.wicam.d_default_custom.list_page;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.ItemFavoriteAsyncTask;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_custom.CustomData;
import com.wicam.d_default_custom.detail_page.CustomDetailActivity;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {

    private CustomListActivity customListActivity;
    private ArrayList<ItemData> customList;

    public CustomListAdapter(CustomListActivity customListActivity, ArrayList<ItemData> customList) {
        this.customListActivity = customListActivity;
        this.customList = customList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_custom_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        CustomData customData = (CustomData)customList.get(position);

        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(customData.isToShowLoading() && position == customList.size() - 1 ? View.VISIBLE : View.GONE);
        holder.cardView.setVisibility(customData.isEmpty() ? View.GONE : View.VISIBLE);
        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!customData.isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 게시물이 없습니다.");
        ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == customList.size() - 1 ? View.VISIBLE : View.GONE);

        holder.customName.setText(customData.getItemName());
        holder.favoriteToggleBtn.setBackgroundResource(customList.get(position).getMyFavorite() == 0 ? R.drawable.item_list_favorite_off : R.drawable.item_list_favorite_on);

        if (customData.getLikes() == 0)
            holder.likes.setVisibility(View.GONE);
        else {
            holder.likes.setVisibility(View.VISIBLE);
            holder.likeNum.setText(String.valueOf(customData.getLikes()));
        }

        if (customData.getComments() == 0)
            holder.comments.setVisibility(View.GONE);
        else {
            holder.comments.setVisibility(View.VISIBLE);
            holder.commentNum.setText(String.valueOf(customData.getComments()));
        }

        if (customData.isEmpty())
            return;

    }

    @Override
    public int getItemCount() {
        return customList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public TextView customName;
        public ImageButton favoriteToggleBtn;
        public RelativeLayout likes, comments;
        public TextView likeNum, commentNum;

        public ViewHolder(View itemView) {
            super(itemView);

            customName = (TextView)itemView.findViewById(R.id.custom_list_name);
            favoriteToggleBtn = (ImageButton)itemView.findViewById(R.id.custom_list_favorite_toggle_button);
            favoriteToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ItemFavoriteAsyncTask(customListActivity, customList, getPosition()).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id="
                            + ((CustomData) customList.get(getPosition())).getItemId() + "&user_id=" + new MyCache(customListActivity).getMyId()
                            + "&default_code=" + new MyCache(customListActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(customListActivity).getContentId() + "&content_type=" + new MyCache(customListActivity).getContentType());
                }
            });

            cardView = (CardView)itemView.findViewById(R.id.custom_list_card_view);
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            customName.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            customName.setTextColor(new WicamColors().TEXT_NORMAL);
                            break;
                        case MotionEvent.ACTION_UP:
                            customName.setTextColor(new WicamColors().TEXT_NORMAL);

                            // 댓글 등을 위해, 어느 아이템인지 페이지에 들어가면서부터 명시
                            //-------------------------------------------------//
                            Singleton.create().setItemPosition(getPosition());
                            Singleton.create().setItemDataList(customList);
                            //-------------------------------------------------//
                            Intent intent = new Intent(customListActivity, CustomDetailActivity.class);
                            customListActivity.startActivityForResult(intent, 0);
                            break;
                    }
                    return false;
                }
            });

            likes = (RelativeLayout)itemView.findViewById(R.id.item_list_like);
            likeNum = (TextView)itemView.findViewById(R.id.item_list_like_count);
            comments = (RelativeLayout)itemView.findViewById(R.id.item_list_comment);
            commentNum = (TextView)itemView.findViewById(R.id.item_list_comment_count);
        }
    }
}

package com.wicam.d_default_delivery.list_page;

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
import com.wicam.d_default_delivery.DeliveryData;
import com.wicam.d_default_delivery.detail_page.DeliveryDetailActivity;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class DeliveryListAdapter extends RecyclerView.Adapter<DeliveryListAdapter.ViewHolder> {

    private DeliveryListActivity deliveryListActivity;
    private ArrayList<ItemData> deliveryList;

    public DeliveryListAdapter(DeliveryListActivity deliveryListActivity, ArrayList<ItemData> deliveryList) {
        this.deliveryListActivity = deliveryListActivity;
        this.deliveryList = deliveryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_delivery_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        DeliveryData deliveryData = (DeliveryData)deliveryList.get(position);

        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(deliveryData.isToShowLoading() && position == deliveryList.size() - 1 ? View.VISIBLE : View.GONE);
        holder.cardView.setVisibility(deliveryData.isEmpty() ? View.GONE : View.VISIBLE);
        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!deliveryData.isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 업체가 없습니다.");
        ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == deliveryList.size() - 1 ? View.VISIBLE : View.GONE);

        holder.deliveryName.setText(deliveryData.getItemName());
        if (deliveryData.getDeliveryCondition().trim().equalsIgnoreCase("")) {
            holder.deliveryCondition.setVisibility(View.GONE);
        }
        else {
            holder.deliveryCondition.setVisibility(View.VISIBLE);
            holder.deliveryCondition.setText(deliveryData.getDeliveryCondition());
        }
        holder.favoriteToggleBtn.setBackgroundResource(deliveryList.get(position).getMyFavorite() == 0 ? R.drawable.item_list_favorite_off : R.drawable.item_list_favorite_on);

        if (deliveryData.getLikes() == 0)
            holder.likes.setVisibility(View.GONE);
        else {
            holder.likes.setVisibility(View.VISIBLE);
            holder.likeNum.setText(String.valueOf(deliveryData.getLikes()));
        }

        if (deliveryData.getComments() == 0)
            holder.comments.setVisibility(View.GONE);
        else {
            holder.comments.setVisibility(View.VISIBLE);
            holder.commentNum.setText(String.valueOf(deliveryData.getComments()));
        }

        if (deliveryData.isEmpty())
            return;

    }

    @Override
    public int getItemCount() {
        return deliveryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public TextView deliveryName, deliveryCondition;
        public ImageButton favoriteToggleBtn;
        public RelativeLayout likes, comments;
        public TextView likeNum, commentNum;

        public ViewHolder(View itemView) {
            super(itemView);

            deliveryName = (TextView)itemView.findViewById(R.id.delivery_list_name);
            deliveryCondition = (TextView)itemView.findViewById(R.id.delivery_list_condition);
            favoriteToggleBtn = (ImageButton)itemView.findViewById(R.id.delivery_list_favorite_toggle_button);
            favoriteToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ItemFavoriteAsyncTask(deliveryListActivity, deliveryList, getPosition()).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id="
                            + ((DeliveryData) deliveryList.get(getPosition())).getItemId() + "&user_id=" + new MyCache(deliveryListActivity).getMyId()
                            + "&default_code=" + new MyCache(deliveryListActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(deliveryListActivity).getContentId() + "&content_type=" + new MyCache(deliveryListActivity).getContentType());
                }
            });

            cardView = (CardView)itemView.findViewById(R.id.delivery_list_card_view);
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            deliveryName.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            deliveryName.setTextColor(new WicamColors().TEXT_NORMAL);
                            break;
                        case MotionEvent.ACTION_UP:
                            deliveryName.setTextColor(new WicamColors().TEXT_NORMAL);

                            // 댓글 등을 위해, 어느 아이템인지 페이지에 들어가면서부터 명시
                            //-------------------------------------------------//
                            Singleton.create().setItemPosition(getPosition());
                            Singleton.create().setItemDataList(deliveryList);
                            //-------------------------------------------------//
                            Intent intent = new Intent(deliveryListActivity, DeliveryDetailActivity.class);
                            deliveryListActivity.startActivityForResult(intent, 0);
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

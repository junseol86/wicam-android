package com.wicam.d_default_advertise.list_page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.ImageAsyncTask;
import com.wicam.a_common_utils.ItemFavoriteAsyncTask;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_advertise.AdvertiseData;
import com.wicam.d_default_advertise.detail_page.AdvertiseDetailActivity;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class AdvertiseListAdapter extends RecyclerView.Adapter<AdvertiseListAdapter.ViewHolder> {

    private AdvertiseListActivity advertiseListActivity;
    private ArrayList<ItemData> advertiseList;

    public AdvertiseListAdapter(AdvertiseListActivity advertiseListActivity, ArrayList<ItemData> advertiseList) {
        this.advertiseListActivity = advertiseListActivity;
        this.advertiseList = advertiseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_advertise_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        AdvertiseData advertiseData = (AdvertiseData)advertiseList.get(position);

        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(advertiseData.isToShowLoading() && position == advertiseList.size() - 1 ? View.VISIBLE : View.GONE);
        holder.cardView.setVisibility(advertiseData.isEmpty() ? View.GONE : View.VISIBLE);
        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!advertiseData.isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 기관·동아리 / 연락처가 없습니다.");
        ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == advertiseList.size() - 1 ? View.VISIBLE : View.GONE);

        holder.advertiseName.setText(advertiseData.getItemName());
        holder.advertiseDate.setText(advertiseData.getModifyTime());
        holder.favoriteToggleBtn.setBackgroundResource(advertiseList.get(position).getMyFavorite() == 0 ? R.drawable.item_list_favorite_off : R.drawable.item_list_favorite_on);

        Display display = advertiseListActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        holder.poster.getLayoutParams().height = (int)((double)screenWidth * 0.4);
        holder.poster.setVisibility(advertiseData.getHasPhoto() == 0 ? View.GONE : View.VISIBLE);
        if (advertiseData.getHasPhoto() == 1) {
            if (advertiseList.get(position).getImage() == null) {
                String photo = "item_" + advertiseData.getItemId();
                new ImageAsyncTask(this, new Security().ADVERTISE_IMAGE, photo, position, holder.poster, advertiseList.get(position)).execute();
            }
            else
                holder.poster.setImageBitmap(advertiseList.get(position).getImage());
        }

        if (advertiseData.isEmpty())
            return;

    }

    @Override
    public int getItemCount() {
        return advertiseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public TextView advertiseName, advertiseDate;
        public ImageButton favoriteToggleBtn;
        public ImageView poster;

        public ViewHolder(View itemView) {
            super(itemView);

            advertiseName = (TextView)itemView.findViewById(R.id.advertise_list_name);
            favoriteToggleBtn = (ImageButton)itemView.findViewById(R.id.advertise_list_favorite_toggle_button);
            favoriteToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ItemFavoriteAsyncTask(advertiseListActivity, advertiseList, getPosition()).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id="
                            + ((AdvertiseData) advertiseList.get(getPosition())).getItemId() + "&user_id=" + new MyCache(advertiseListActivity).getMyId()
                            + "&default_code=" + new MyCache(advertiseListActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(advertiseListActivity).getContentId() + "&content_type=" + new MyCache(advertiseListActivity).getContentType());
                }
            });

            advertiseDate = (TextView)itemView.findViewById(R.id.advertise_list_date);

            poster = (ImageView)itemView.findViewById(R.id.advertise_list_poster);

            cardView = (CardView)itemView.findViewById(R.id.advertise_list_card_view);
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            advertiseName.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            advertiseName.setTextColor(new WicamColors().TEXT_NORMAL);
                            break;
                        case MotionEvent.ACTION_UP:
                            advertiseName.setTextColor(new WicamColors().TEXT_NORMAL);

                            // 댓글 등을 위해, 어느 아이템인지 페이지에 들어가면서부터 명시
                            //-------------------------------------------------//
                            Singleton.create().setItemPosition(getPosition());
                            Singleton.create().setItemDataList(advertiseList);
                            //-------------------------------------------------//
                            Intent intent = new Intent(advertiseListActivity, AdvertiseDetailActivity.class);
                            advertiseListActivity.startActivityForResult(intent, 0);
                            break;
                    }
                    return false;
                }
            });

        }
    }
}

package com.wicam.d_default_restaurant.list_page;

import android.content.Intent;
import android.graphics.Point;
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
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.d_default_restaurant.detail_page.RestaurantDetailActivity;
import com.wicam.d_default_restaurant.RestaurantData;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder> {

    private RestaurantListActivity restaurantListActivity;
    private ArrayList<ItemData> restaurantList;

    public RestaurantListAdapter(RestaurantListActivity restaurantListActivity, ArrayList<ItemData> restaurantList) {
        this.restaurantListActivity = restaurantListActivity;
        this.restaurantList = restaurantList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_restaurant_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        RestaurantData restaurantData = (RestaurantData)restaurantList.get(position);

        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(restaurantData.isToShowLoading() && position == restaurantList.size() - 1 ? View.VISIBLE : View.GONE);
        holder.cardView.setVisibility(restaurantData.isEmpty() ? View.GONE : View.VISIBLE);
        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!restaurantData.isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 식당이나 카페가 없습니다.");
        ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == restaurantList.size() - 1 ? View.VISIBLE : View.GONE);

        holder.restaurantName.setText(restaurantData.getItemName());

        holder.address.setText(restaurantData.getAddress());
        holder.favoriteToggleBtn.setBackgroundResource(restaurantList.get(position).getMyFavorite() == 0 ? R.drawable.restaurant_list_favorite_toggle_off : R.drawable.restaurant_list_favorite_toggle_on);

        if (restaurantData.getLikes() == 0)
            holder.like.setVisibility(View.GONE);
        else {
            holder.like.setVisibility(View.VISIBLE);
            holder.likeNum.setText("×" + String.valueOf(restaurantData.getLikes()));
        }

        if (restaurantData.isEmpty())
            return;


        Display display = restaurantListActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        holder.cardView.getLayoutParams().height = (int)((double)screenWidth * 0.4);

        if (restaurantList.get(position).getImage() == null) {
            String photo = restaurantData.getMainPhoto();

            // 식당에 댓글사진이 없을 경우 장르사진으로 리스트사진을 대체
            if (photo.equalsIgnoreCase(""))
                switch (restaurantData.getGenre()) {
                    case "한식": photo = "korean"; break;
                    case "분식": photo = "snack"; break;
                    case "중식": photo = "chinese"; break;
                    case "일식": photo = "japanese"; break;
                    case "양식": photo = "western"; break;
                    case "세계": photo = "world"; break;
                    case "고기": photo = "meat"; break;
                    case "치킨": photo = "chicken"; break;
                    case "카페": photo = "cafe"; break;
                    case "주점": photo = "bar"; break;
                }

            new ImageAsyncTask(this, new Security().RESTAURANT_IMAGE, photo, position, holder.imageView, restaurantList.get(position)).execute();
        }
        else
            holder.imageView.setImageBitmap(restaurantList.get(position).getImage());

    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public ImageView imageView;
        public TextView restaurantName, address;
        public ImageButton favoriteToggleBtn;
        public RelativeLayout like;
        public TextView likeNum;

        public ViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView)itemView.findViewById(R.id.restaurant_list_card_view);
            imageView = (ImageView)itemView.findViewById(R.id.restaurant_list_image);
            restaurantName = (TextView)itemView.findViewById(R.id.restaurant_list_name);
            address = (TextView)itemView.findViewById(R.id.restaurant_list_address);
            favoriteToggleBtn = (ImageButton)itemView.findViewById(R.id.restaurant_list_favorite_toggle_button);
            favoriteToggleBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new ItemFavoriteAsyncTask(restaurantListActivity, restaurantList, getPosition()).execute(new Security().WEB_ADDRESS + "item_favorite_toggle.php?item_id="
                            + ((RestaurantData) restaurantList.get(getPosition())).getItemId() + "&user_id=" + new MyCache(restaurantListActivity).getMyId()
                            + "&default_code=" + new MyCache(restaurantListActivity).getDefaultCode()
                            + "&content_id=" + new MyCache(restaurantListActivity).getContentId() + "&content_type=" + new MyCache(restaurantListActivity).getContentType());
                }
            });

            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            restaurantName.setTextColor(new WicamColors().YELLOW);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            restaurantName.setTextColor(new WicamColors().WHITE);
                            break;
                        case MotionEvent.ACTION_UP:
                            restaurantName.setTextColor(new WicamColors().WHITE);

                            // 댓글 등을 위해, 어느 아이템인지 페이지에 들어가면서부터 명시
                            //-------------------------------------------------//
                            Singleton.create().setItemPosition(getPosition());
                            Singleton.create().setItemDataList(restaurantList);
                            //-------------------------------------------------//
                            Intent intent = new Intent(restaurantListActivity, RestaurantDetailActivity.class);
                            restaurantListActivity.startActivityForResult(intent, 0);
                            break;
                    }
                    return false;
                }
            });

            like = (RelativeLayout)itemView.findViewById(R.id.restaurant_list_like);
            likeNum = (TextView)itemView.findViewById(R.id.restaurant_list_like_text);
        }

    }
}

package com.wicam.c_main_page.content_drawer;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wicam.R;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class ContentListAdapter extends RecyclerView.Adapter<ContentListAdapter.ViewHolder> {

    private ContentDrawer contentDrawer;
    private ArrayList<ContentListData> contentList;
    private SharedPreferences cache;
    private boolean longClicking = false;

    public ContentListAdapter(ContentDrawer contentDrawer, ArrayList<ContentListData> contentList) {
        this.contentDrawer = contentDrawer;
        this.contentList = contentList;
        this.cache = contentDrawer.getMainPage().getSharedPreferences("wicam", Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_main_page_content_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        holder.contentListItemLl.setBackgroundColor(position % 2 == 0 ? new WicamColors().WHITE : new WicamColors().WC_VERY_LIGHT_BLUE);
        holder.contentName.setText(contentList.get(position).getContent_name());
        holder.description.setText(contentList.get(position).getDescription());

        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(contentList.get(position).isToShowLoading() && position == contentList.size() - 1 ? View.VISIBLE : View.GONE);

        switch (contentList.get(position).getContent_type()) {
            case "1": holder.pictogram.setImageResource(R.drawable.pictogram_custom); break;
            case "2": holder.pictogram.setImageResource(R.drawable.pictogram_web); break;
            case "3": holder.pictogram.setImageResource(R.drawable.pictogram_android); break;
            default: holder.pictogram.setImageResource(R.drawable.pictogram_wicam); break;
        }

        if (contentList.get(position).getFavorite() == 1) {
            holder.favoriteButton.setBackgroundResource(R.drawable.content_favorite_on);
            holder.description.setVisibility(View.GONE);
        }
        else {
            holder.favoriteButton.setBackgroundResource(R.drawable.content_favorite_off);
            holder.description.setVisibility(View.VISIBLE);
        }

        ((RelativeLayout)holder.itemView.findViewById(R.id.content_drawer_last)).setVisibility(position == contentList.size() - 1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnLongClickListener {
        public LinearLayout contentListItemLl;
        public TextView contentName, description;
        public ImageButton favoriteButton;
        public ImageView pictogram;

        public ViewHolder(View itemView) {
            super(itemView);
            contentListItemLl = (LinearLayout)itemView.findViewById(R.id.content_list_ll);
            contentName = (TextView)itemView.findViewById(R.id.content_list_content_name);
            description = (TextView)itemView.findViewById(R.id.content_list_desciption);

            pictogram = (ImageView)itemView.findViewById(R.id.content_pictogram);

            favoriteButton = (ImageButton)itemView.findViewById(R.id.content_list_favorite_button);
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contentDrawer.setFavoriteUpFalse();
                    new ContentFavoriteAsyncTask(contentDrawer, contentList, getPosition()).execute(new Security().WEB_ADDRESS +
                            "content_favorite_toggle.php?content_id=" + contentList.get(getPosition()).getContent_id() + "&user_id=" + new MyCache(contentDrawer.getMainPage()).getMyId()
                    );
                }
            });

            contentListItemLl.setOnTouchListener(this);
            contentListItemLl.setOnLongClickListener(this);

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    contentName.setTextColor(new WicamColors().WC_BLUE); break;
                case MotionEvent.ACTION_CANCEL:
                    contentName.setTextColor(new WicamColors().TEXT_NORMAL); break;
                case MotionEvent.ACTION_UP:
                    contentName.setTextColor(new WicamColors().TEXT_NORMAL);

                    if (longClicking) {
                        longClicking = false;
                        return false;
                    }

                    contentDrawer.onItemClick(getPosition());
                    break;
            }
            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            longClicking = true;
            contentDrawer.onItemLoinClick(getPosition());
            return false;
        }
    }
}

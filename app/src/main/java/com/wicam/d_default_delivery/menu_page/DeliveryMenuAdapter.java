package com.wicam.d_default_delivery.menu_page;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.WicamColors;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class DeliveryMenuAdapter extends RecyclerView.Adapter<DeliveryMenuAdapter.ViewHolder> {

    private DeliveryMenuActivity deliveryMenuActivity;
    private ArrayList<DeliveryMenuData> deliveryMenuDataArrayList;

    public DeliveryMenuAdapter(DeliveryMenuActivity deliveryMenuActivity, ArrayList<DeliveryMenuData> deliveryMenuDataArrayList) {
        this.deliveryMenuActivity = deliveryMenuActivity;
        this.deliveryMenuDataArrayList = deliveryMenuDataArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_delivery_menu_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DeliveryMenuData deliveryMenuData = deliveryMenuDataArrayList.get(position);

        if (deliveryMenuData.getGroupOrMenu() == 0) {
            holder.groupRl.setVisibility(View.VISIBLE);
            holder.menuRl.setVisibility(View.GONE);
            holder.groupName.setText(deliveryMenuData.getGroupName());
            holder.groupPrice.setText(deliveryMenuData.getGroupPrice());
            holder.groupDesc.setVisibility(deliveryMenuData.getGroupDesc().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            holder.groupDesc.setText(deliveryMenuData.getGroupDesc());
        }
        else {
            holder.groupRl.setVisibility(View.GONE);
            holder.menuRl.setVisibility(View.VISIBLE);
            holder.menuRl.setBackgroundColor(position % 2 == 0 ? new WicamColors().WHITE : new WicamColors().WC_VERY_LIGHT_BLUE);
            holder.menuName.setText(deliveryMenuData.getMenuName());
            holder.menuPrice.setText(deliveryMenuData.getMenuPrice());
            holder.menuDesc.setVisibility(deliveryMenuData.getMenuDesc().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            holder.menuDesc.setText(deliveryMenuData.getMenuDesc());
        }
    }

    @Override
    public int getItemCount() {
        return deliveryMenuDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout groupRl, menuRl;
        public TextView groupName, groupDesc, groupPrice, menuName, menuDesc, menuPrice;
        public ViewHolder(View itemView) {
            super(itemView);
            groupRl = (RelativeLayout)itemView.findViewById(R.id.group);
            groupName = (TextView)itemView.findViewById(R.id.group_name);
            groupDesc = (TextView)itemView.findViewById(R.id.group_desc);
            groupPrice = (TextView)itemView.findViewById(R.id.group_price);
            menuRl = (RelativeLayout)itemView.findViewById(R.id.menu);
            menuName = (TextView)itemView.findViewById(R.id.menu_name);
            menuDesc = (TextView)itemView.findViewById(R.id.menu_desc);
            menuPrice = (TextView)itemView.findViewById(R.id.menu_price);
        }
    }
}

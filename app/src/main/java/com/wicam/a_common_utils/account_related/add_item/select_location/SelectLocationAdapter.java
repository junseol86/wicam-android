package com.wicam.a_common_utils.account_related.add_item.select_location;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.WicamColors;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class SelectLocationAdapter extends RecyclerView.Adapter<SelectLocationAdapter.ViewHolder> {

    SelectLocationActivity selectLocationActivity;

    public SelectLocationAdapter(SelectLocationActivity selectLocationActivity) {
        this.selectLocationActivity = selectLocationActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_common_select_location_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.notEmpty.setVisibility(selectLocationActivity.getLocationList().get(position).isEmpty() ? View.GONE : View.VISIBLE);
        if (!selectLocationActivity.getLocationList().get(position).isEmpty()) {
            holder.locationTitle.setText(selectLocationActivity.getLocationList().get(position).getTitle());
            holder.notEmpty.setBackgroundColor(position % 2 == 0 ? new WicamColors().WHITE : new WicamColors().WC_VERY_LIGHT_BLUE);
        }
        holder.empty.setVisibility(!selectLocationActivity.getLocationList().get(position).isEmpty() ? View.GONE : View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return selectLocationActivity.getLocationList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout notEmpty, empty;
        public TextView locationTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            notEmpty = (RelativeLayout)itemView.findViewById(R.id.select_location_item_not_empty);
            empty = (RelativeLayout)itemView.findViewById(R.id.select_location_item_empty);
            locationTitle = (TextView)itemView.findViewById(R.id.location_list_title);

            notEmpty.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            locationTitle.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_UP:
                            locationTitle.setTextColor(new WicamColors().TEXT_NORMAL);
                            selectLocationActivity.selectRegion(selectLocationActivity.getLocationList().get(getPosition()).getLatitude(), selectLocationActivity.getLocationList().get(getPosition()).getLongitude());
                            break;
                    }
                    return false;
                }
            });
        }
    }
}

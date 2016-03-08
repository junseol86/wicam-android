package com.wicam.a_common_utils.account_related.add_item.select_schools;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
public class SelectSchoolsAdapter extends RecyclerView.Adapter<SelectSchoolsAdapter.ViewHolder> {

    private SelectSchoolsActivity selectSchoolActivity;
    private ArrayList<SchoolsIdNameData> downloadedSchoolList;
    private ArrayList<String> selectedSchoolIdList;

    public SelectSchoolsAdapter(SelectSchoolsActivity selectSchoolActivity) {
        this.selectSchoolActivity = selectSchoolActivity;
        this.downloadedSchoolList = selectSchoolActivity.getDownloadedSchoolList();
        this.selectedSchoolIdList = selectSchoolActivity.getSelectedSchoolIdList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_common_select_schools_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (selectedSchoolIdList.contains(downloadedSchoolList.get(position).getSchoolId())) {
            holder.schoolItemRl.setBackgroundColor(position % 2 == 0 ? new WicamColors().WC_SEMI_BLUE : new WicamColors().WC_BLUE);
            holder.schoolName.setTextColor(new WicamColors().WHITE);
        }
        else {
            holder.schoolItemRl.setBackgroundColor(position % 2 == 0 ? new WicamColors().WHITE : new WicamColors().WC_VERY_LIGHT_BLUE);
            holder.schoolName.setTextColor(new WicamColors().TEXT_NORMAL);
        }

        holder.schoolName.setText(downloadedSchoolList.get(position).getSchoolName());
    }

    @Override
    public int getItemCount() {
        return selectSchoolActivity.getDownloadedSchoolList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        public RelativeLayout schoolItemRl;
        public TextView schoolName;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            schoolItemRl = (RelativeLayout)itemView.findViewById(R.id.select_schools_item_rl);
            schoolName = (TextView)itemView.findViewById(R.id.select_schools_item_text);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    schoolName.setTextColor(selectedSchoolIdList.contains(downloadedSchoolList.get(getPosition()).getSchoolId()) ? new WicamColors().TEXT_NORMAL : new WicamColors().WC_BLUE); break;
                case MotionEvent.ACTION_CANCEL:
                    schoolName.setTextColor(selectedSchoolIdList.contains(downloadedSchoolList.get(getPosition()).getSchoolId()) ? new WicamColors().WHITE : new WicamColors().TEXT_NORMAL);
                    break;
                case MotionEvent.ACTION_UP:
                    schoolName.setTextColor(selectedSchoolIdList.contains(downloadedSchoolList.get(getPosition()).getSchoolId()) ? new WicamColors().WHITE : new WicamColors().TEXT_NORMAL);
                    schoolName.setTextColor(new WicamColors().TEXT_NORMAL);
                    if (selectedSchoolIdList.contains(downloadedSchoolList.get(getPosition()).getSchoolId())) {
                        selectedSchoolIdList.remove(downloadedSchoolList.get(getPosition()).getSchoolId());
                    }
                    else {
                        if (selectedSchoolIdList.size() < 10) {
                            selectedSchoolIdList.add(downloadedSchoolList.get(getPosition()).getSchoolId());
                        }
                    }
                    selectSchoolActivity.selectRefresh();
                    break;
            }
            return false;
        }
    }
}

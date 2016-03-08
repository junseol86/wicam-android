package com.wicam.b_select_schools_page;

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
public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.ViewHolder> {

    private SelectSchoolActivity selectSchoolActivity;
    private ArrayList<SchoolData> schoolList;

    public SchoolAdapter(SelectSchoolActivity selectSchoolActivity, ArrayList<SchoolData> schoolList) {
        this.selectSchoolActivity = selectSchoolActivity;
        this.schoolList = schoolList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.b_select_school_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.schoolItemRl.setBackgroundColor(position % 2 == 0 ? new WicamColors().WHITE : new WicamColors().WC_VERY_LIGHT_BLUE);
        holder.schoolName.setText(schoolList.get(position).getSchoolName());
        holder.schoolContents.setText(schoolList.get(position).getSchoolContents());
    }

    @Override
    public int getItemCount() {
        return schoolList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        public RelativeLayout schoolItemRl;
        public TextView schoolName, schoolContents;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(this);
            schoolItemRl = (RelativeLayout)itemView.findViewById(R.id.school_list_rl);
            schoolName = (TextView)itemView.findViewById(R.id.school_name);
            schoolContents = (TextView)itemView.findViewById(R.id.school_contents);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    schoolName.setTextColor(new WicamColors().WC_BLUE); break;
                case MotionEvent.ACTION_CANCEL:
                    schoolName.setTextColor(new WicamColors().TEXT_NORMAL); break;
                case MotionEvent.ACTION_UP:
                    schoolName.setTextColor(new WicamColors().TEXT_NORMAL);
                    selectSchoolActivity.selectSchool(getPosition());
                    break;
            }
            return false;
        }
    }
}

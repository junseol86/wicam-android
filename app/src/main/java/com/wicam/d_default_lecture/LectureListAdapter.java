package com.wicam.d_default_lecture;

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

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class LectureListAdapter extends RecyclerView.Adapter<LectureListAdapter.ViewHolder> {

    private LectureListActivity lectureListActivity;
    private ArrayList<ItemData> lectureList;

    public LectureListAdapter(LectureListActivity lectureListActivity, ArrayList<ItemData> lectureList) {
        this.lectureListActivity = lectureListActivity;
        this.lectureList = lectureList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_lecture_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        LectureData lectureData = (LectureData)lectureList.get(position);

        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(lectureData.isToShowLoading() && position == lectureList.size() - 1 ? View.VISIBLE : View.GONE);
        holder.cardView.setVisibility(lectureData.isEmpty() ? View.GONE : View.VISIBLE);
        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!lectureData.isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 강의가 없습니다.");
        ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == lectureList.size() - 1 ? View.VISIBLE : View.GONE);
        if (lectureData.getAvgDifficulty() == 0.0 || lectureData.getAvgInstructiveness() == 0.0) {
            holder.values.setText("");
        }
        else {
            String difficulty = String.valueOf(lectureData.getAvgDifficulty() + 0.05);
            String instructiveness = String.valueOf(lectureData.getAvgInstructiveness() + 0.05);
            if (difficulty.length() < 3)
                difficulty += ".0";
            else
                difficulty = difficulty.substring(0, 3);

            if (instructiveness.length() < 3)
                instructiveness += ".0";
            else
                instructiveness = instructiveness.substring(0, 3);

            // 10이라면 10. 을 10.0으로
            if (difficulty.charAt(2) == '.')
                difficulty += "0";
            if (instructiveness.charAt(2) == '.')
                instructiveness += "0";
            holder.values.setText(difficulty + " | " + instructiveness);
        }

        holder.lectureName.setText(lectureData.getItemName());
        holder.professorName.setText(lectureData.getProfessorName() + " 교수님");

        if (lectureData.isEmpty())
            return;

    }

    @Override
    public int getItemCount() {
        return lectureList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public TextView lectureName, professorName, values;

        public ViewHolder(View itemView) {
            super(itemView);

            lectureName = (TextView)itemView.findViewById(R.id.lecture_list_name);
            professorName = (TextView)itemView.findViewById(R.id.lecture_list_professor);
            values = (TextView)itemView.findViewById(R.id.lecture_list_values);

            cardView = (CardView)itemView.findViewById(R.id.lecture_list_card_view);
            cardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lectureName.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            lectureName.setTextColor(new WicamColors().TEXT_NORMAL);
                            break;
                        case MotionEvent.ACTION_UP:
                            lectureName.setTextColor(new WicamColors().TEXT_NORMAL);

                            // 댓글 등을 위해, 어느 아이템인지 페이지에 들어가면서부터 명시
                            //-------------------------------------------------//
                            Singleton.create().setItemPosition(getPosition());
                            Singleton.create().setItemDataList(lectureList);
                            //-------------------------------------------------//
                            Intent intent = new Intent(lectureListActivity, LectureDetailActivity.class);
                            lectureListActivity.startActivityForResult(intent, 0);
                            break;
                    }
                    return false;
                }
            });

        }
    }
}

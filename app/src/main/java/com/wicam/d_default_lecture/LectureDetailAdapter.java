package com.wicam.d_default_lecture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class LectureDetailAdapter extends RecyclerView.Adapter<LectureDetailAdapter.ViewHolder> {

    private DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    private LectureDetailActivity lectureDetailActivity;
    private LectureData lectureDetailData;
    private Activity activity;
    private ArrayList<ItemAndCommentData> itemAndCommentDataList;
    private int screenWidth;

    public LectureDetailAdapter(Activity activity, DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, ArrayList<ItemAndCommentData> itemAndCommentDataList) {
        this.activity = activity;
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.lectureDetailActivity = (LectureDetailActivity) detailActivityWithCommentAndReport;
        this.itemAndCommentDataList = itemAndCommentDataList;

        Display display = lectureDetailActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.d_lecture_detail_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        holder.lectureInfo.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        holder.itemView.findViewById(R.id.assess_ll).setVisibility(position != 0 ? View.VISIBLE : View.GONE);

        if (position == 0) { // 정보일 경우
            lectureDetailData = (LectureData)itemAndCommentDataList.get(position);
            holder.writeTime.setText(lectureDetailData.getModifyTime());
            holder.lectureName.setText(lectureDetailData.getItemName());
            holder.professorName.setText(lectureDetailData.getProfessorName() + " 교수님");
            holder.major.setText(lectureDetailData.getMajor().trim().equalsIgnoreCase("") ? "교양" : lectureDetailData.getMajor());

            holder.deleteButton.setVisibility(new MyCache(lectureDetailActivity).getMyId().equalsIgnoreCase(lectureDetailData.getWriterId()) ? View.VISIBLE : View.GONE);

            if (lectureDetailData.getAvgDifficulty() == 0.0 && lectureDetailData.getAvgInstructiveness() == 0.0) {
                holder.averageValues.setText("평가를 입력해주세요.");
            }
            else {
                String difficulty = String.valueOf(lectureDetailData.getAvgDifficulty() + 0.05);
                String instructiveness = String.valueOf(lectureDetailData.getAvgInstructiveness() + 0.05);
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
                holder.averageValues.setText("어려움 평균: " + difficulty + "   |   유익함 평균: " + instructiveness);
            }

        }
        else { // 평가일 경우
            LectureAssessData assessData = (LectureAssessData)itemAndCommentDataList.get(position);
            
            ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(assessData.isToShowLoading() && position == itemAndCommentDataList.size() - 1 ? View.VISIBLE : View.GONE);
            holder.assessCardView.setVisibility(assessData.isEmpty() ? View.GONE : View.VISIBLE);
            ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 입력된 평가가 없습니다.");
            ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!assessData.isEmpty() ? View.GONE : View.VISIBLE);
            ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == itemAndCommentDataList.size() - 1 ? View.VISIBLE : View.GONE);

            holder.assessTime.setText(assessData.getWriteTime());
            holder.lectureDescription.setVisibility(assessData.getDescription().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
            holder.lectureDescription.setText(assessData.getDescription());

            holder.setStars(holder.difficultyList, assessData.getDifficulty());
            holder.setStars(holder.instructivenessList, assessData.getInstructiveness());
        }

    }

    @Override
    public int getItemCount() {
        return itemAndCommentDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout lectureInfo;
        private TextView writeTime, lectureName, professorName, major, averageValues, assessTime, lectureDescription;
        private Button deleteButton;
        private CardView assessCardView;
        private ArrayList<ImageView> difficultyList = new ArrayList<ImageView>();
        private ArrayList<ImageView> instructivenessList = new ArrayList<ImageView>();

        public ViewHolder(View itemView) {
            super(itemView);
            lectureInfo = (LinearLayout)itemView.findViewById(R.id.lecture_detail_lecture_information);
            writeTime = (TextView)itemView.findViewById(R.id.lecture_detail_write_time);
            lectureName = (TextView)itemView.findViewById(R.id.lecture_detail_lecture_name);
            professorName = (TextView)itemView.findViewById(R.id.lecture_detail_professor_name);
            major = (TextView)itemView.findViewById(R.id.lecture_detail_major);
            averageValues = (TextView)itemView.findViewById(R.id.lecture_detail_average_values);

            assessCardView = (CardView)itemView.findViewById(R.id.assess_card_view);
            assessTime = (TextView)itemView.findViewById(R.id.lecture_assess_time);
            lectureDescription = (TextView)itemView.findViewById(R.id.lecture_description);

            difficultyList.add((ImageView)itemView.findViewById(R.id.assess_difficulty_1));
            difficultyList.add((ImageView)itemView.findViewById(R.id.assess_difficulty_2));
            difficultyList.add((ImageView)itemView.findViewById(R.id.assess_difficulty_3));
            difficultyList.add((ImageView)itemView.findViewById(R.id.assess_difficulty_4));
            difficultyList.add((ImageView)itemView.findViewById(R.id.assess_difficulty_5));

            instructivenessList.add((ImageView)itemView.findViewById(R.id.assess_instructiveness_1));
            instructivenessList.add((ImageView)itemView.findViewById(R.id.assess_instructiveness_2));
            instructivenessList.add((ImageView)itemView.findViewById(R.id.assess_instructiveness_3));
            instructivenessList.add((ImageView)itemView.findViewById(R.id.assess_instructiveness_4));
            instructivenessList.add((ImageView)itemView.findViewById(R.id.assess_instructiveness_5));

            deleteButton = (Button)itemView.findViewById(R.id.lecture_delete_btn);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lectureDetailData.getAuthority() == 1) {
                        new AlertDialog.Builder(lectureDetailActivity)
                                .setMessage("정보를 정말로 삭제하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new LectureDeleteAsyncTask(lectureDetailActivity).execute(new Security().WEB_ADDRESS + "delete_lecture.php?user_id=" + new MyCache(lectureDetailActivity).getMyId()
                                                        + "&device_id=" + new MyCache(lectureDetailActivity).getDeviceId() + "&item_id=" + ((LectureData)itemAndCommentDataList.get(0)).getItemId()
                                        );
                                    }
                                })
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    }
                    else {
                        new AlertDialog.Builder(lectureDetailActivity)
                                .setMessage("강의정보는 작성자만 삭제할 수 있습니다.")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    }
                }
            });
        }

        public void setStars(ArrayList<ImageView> starList, int difficulty) {
            for (int i = 0; i < starList.size(); i++) {
                if (difficulty >= (i + 1) * 2)
                    starList.get(i).setImageResource(R.drawable.assess_star_full);
                else if (difficulty == ((i + 1) * 2 - 1))
                    starList.get(i).setImageResource(R.drawable.assess_star_half);
                else
                    starList.get(i).setImageResource(R.drawable.assess_star_empty);
            }
        }
    }
}

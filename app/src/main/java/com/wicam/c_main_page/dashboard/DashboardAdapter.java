package com.wicam.c_main_page.dashboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.ImageAsyncTask;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemAndCommentData;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.c_main_page.MainPage;
import com.wicam.d_default_lecture.LectureAssessData;
import com.wicam.d_default_lecture.LectureData;
import com.wicam.d_default_lecture.LectureDeleteAsyncTask;
import com.wicam.d_default_lecture.LectureDetailActivity;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Hyeonmin on 2015-07-10.
 */
public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    private MainPage mainPage;
    private ArrayList<DashboardData> dashboardList;
    private int screenWidth;
    private MyCache myCache;
    private String today, yesterday, dbYesterday;

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
    Calendar cal = Calendar.getInstance();

    public DashboardAdapter(MainPage mainPage, ArrayList<DashboardData> dashboardList) {
        this.mainPage = mainPage;
        this.myCache = new MyCache(mainPage);
        this.dashboardList = dashboardList;

        Display display = mainPage.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        today = formatter.format(cal.getTime()).toString();
        cal.add(Calendar.DATE, -1);
        yesterday = formatter.format(cal.getTime()).toString();
        cal.add(Calendar.DATE, -1);
        dbYesterday = formatter.format(cal.getTime()).toString();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_main_page_dashboard_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (!Singleton.create().isCanBindView()) // 필터를 빨리 바꾸던가 해도 리스트를 다 받기 전에는 BindView하지 안도록
            return;

        DashboardData dashboardData = (DashboardData)dashboardList.get(position);

        ((RelativeLayout) holder.itemView.findViewById(R.id.recyclerview_loading)).setVisibility(dashboardData.isToShowLoading() && position == dashboardList.size() - 1 ? View.VISIBLE : View.GONE);
        holder.dashboardCardView.setVisibility(dashboardData.isEmpty() ? View.GONE : View.VISIBLE);
        ((TextView)holder.itemView.findViewById(R.id.empty_word)).setText("아직 컨텐츠가 없습니다.");
        ((RelativeLayout)holder.itemView.findViewById(R.id.recyclerview_empty)).setVisibility(!dashboardData.isEmpty() ? View.GONE : View.VISIBLE);
        ((Space)holder.itemView.findViewById(R.id.recyclerview_last_space)).setVisibility(position == dashboardList.size() - 1 ? View.VISIBLE : View.GONE);


        ((LinearLayout)holder.itemView.findViewById(R.id.dashboard_assess)).setVisibility(dashboardData.getContent().equalsIgnoreCase("lecture") ? View.VISIBLE : View.GONE);
        if (dashboardData.getContent().equalsIgnoreCase("lecture")) {
            holder.setStars(holder.difficultyList, Integer.parseInt(dashboardData.getValue1()));
            holder.setStars(holder.instructivenessList, Integer.parseInt(dashboardData.getValue2()));
        }

        ((RelativeLayout)holder.itemView.findViewById(R.id.dashboard_school_rl)).setVisibility(
                dashboardData.getSchool().contains(myCache.getMySchoolName()) ?
                        View.GONE : View.VISIBLE
        );
        holder.school.setText(dashboardData.getSchool());

        if (dashboardData.getContent().equalsIgnoreCase("lecture")) {
            holder.title.setText("[" + dashboardData.getPerson() + " 교수님]\n" + dashboardData.getTitle());
            holder.person.setText("익명");
        }
        else {
            holder.title.setText(dashboardData.getTitle());
            holder.person.setText(dashboardData.getPerson());
        }

        System.out.println(dashboardData.getTime().substring(0, 10) + today);
        if (dashboardData.getTime().substring(0, 10).equalsIgnoreCase(today)) {
            holder.time.setTextColor(new WicamColors().WC_BLUE);
            holder.time.setText("오늘" + dashboardData.getTime().substring(10, dashboardData.getTime().length()));
        }
        else if (dashboardData.getTime().substring(0, 10).equalsIgnoreCase(yesterday)) {
            holder.time.setTextColor(new WicamColors().TEXT_VERY_LIGHT);
            holder.time.setText("어제" + dashboardData.getTime().substring(10, dashboardData.getTime().length()));
        }
        else if (dashboardData.getTime().substring(0, 10).equalsIgnoreCase(dbYesterday)) {
            holder.time.setTextColor(new WicamColors().TEXT_VERY_LIGHT);
            holder.time.setText("그제" + dashboardData.getTime().substring(10, dashboardData.getTime().length()));
        }
        else {
            holder.time.setTextColor(new WicamColors().TEXT_VERY_LIGHT);
            holder.time.setText(dashboardData.getTime());
        }

        holder.text.setVisibility(dashboardData.getText().trim().equalsIgnoreCase("") ? View.GONE : View.VISIBLE);
        if (dashboardData.getContent().equalsIgnoreCase("custom")) {
            holder.text.setText(dashboardData.getValue1().length() > 100 ? dashboardData.getText() + "\n\n" + dashboardData.getValue1().substring(0, 99) + "…" : dashboardData.getText() + "\n\n" + dashboardData.getValue1());
        }
        else {
            holder.text.setText(dashboardData.getText().length() > 100 ? dashboardData.getText().substring(0, 99) + "…" : dashboardData.getText());
        }

        if ((dashboardData.getContent().equalsIgnoreCase("delivery") || dashboardData.getContent().equalsIgnoreCase("restaurant")) && !dashboardData.getValue1().trim().equalsIgnoreCase("")) {
            ((RelativeLayout)holder.itemView.findViewById(R.id.dashboard_url_rl)).setVisibility(View.VISIBLE);
            holder.url.setText(dashboardData.getValue1());
        }
        else
            ((RelativeLayout)holder.itemView.findViewById(R.id.dashboard_url_rl)).setVisibility(View.GONE);

        ((RelativeLayout)holder.itemView.findViewById(R.id.dashboard_photo_rl)).setVisibility(dashboardData.getHasPic() == 1 ? View.VISIBLE : View.GONE);
        if (dashboardData.getHasPic() == 1) {

            String address = "";
            String file = "";
            switch (dashboardData.getContent()) {
                case "advertise":
                    address = new Security().ADVERTISE_IMAGE;
                    file = "item_" + dashboardData.getPhoto_idx();
                    break;
                case "custom":
                    address = new Security().CUSTOM_IMAGE;
                    file = "item_" + dashboardData.getPhoto_idx();
                    break;
                case "delivery":
                    address = new Security().DELIVERY_IMAGE;
                    file = dashboardData.getPhoto_idx();
                    break;
                case "restaurant":
                    address = new Security().RESTAURANT_IMAGE;
                    file = dashboardData.getPhoto_idx();
                    break;
            }

            holder.photo.getLayoutParams().height = (int)((double)screenWidth * 0.4);
            if (dashboardData.getImage() == null)
                new ImageAsyncTask(this, address, file, position, holder.photo, dashboardData).execute();
            else {
                holder.photo.setImageBitmap(dashboardData.getImage());
            }
        }

    }

    @Override
    public int getItemCount() {
        return dashboardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, text, time, person, url, school;
        private CardView dashboardCardView;
        private ArrayList<ImageView> difficultyList = new ArrayList<ImageView>();
        private ArrayList<ImageView> instructivenessList = new ArrayList<ImageView>();
        private ImageView photo;

        public ViewHolder(View itemView) {
            super(itemView);

            school = (TextView)itemView.findViewById(R.id.dashboard_school);
            title = (TextView)itemView.findViewById(R.id.dashboard_title);
            text = (TextView)itemView.findViewById(R.id.dashboard_text);
            person = (TextView)itemView.findViewById(R.id.dashboard_person);
            time = (TextView)itemView.findViewById(R.id.dashboard_time);
            url = (TextView)itemView.findViewById(R.id.dashboard_url);

            dashboardCardView = (CardView)itemView.findViewById(R.id.dashboard_card_view);

            photo = (ImageView)itemView.findViewById(R.id.dashboard_photo);

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

            dashboardCardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: title.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL: title.setTextColor(new WicamColors().TEXT_NORMAL);
                            break;
                        case MotionEvent.ACTION_UP: title.setTextColor(new WicamColors().TEXT_NORMAL);
                            mainPage.intoContent(dashboardList.get(getPosition()));
                            break;
                    }
                    return false;
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

package com.wicam.c_main_page;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wicam.R;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.a_common_utils.scroll_to_update.ActivityWithScrollUpdate;
import com.wicam.b_select_schools_page.SelectSchoolActivity;
import com.wicam.c_main_page.content_drawer.ContentDrawer;
import com.wicam.c_main_page.dashboard.DashboardAdapter;
import com.wicam.c_main_page.dashboard.DashboardAsyncTask;
import com.wicam.c_main_page.dashboard.DashboardData;
import com.wicam.d_default_advertise.list_page.AdvertiseListActivity;
import com.wicam.d_default_custom.list_page.CustomListActivity;
import com.wicam.d_default_delivery.list_page.DeliveryListActivity;
import com.wicam.d_default_lecture.LectureListActivity;
import com.wicam.d_default_restaurant.list_page.RestaurantListActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-12.
 */
public class MainPage extends ActivityWithScrollUpdate {

    private ContentDrawer contentDrawer;
    private ArrayList<DashboardData> dashboardList = new ArrayList<DashboardData>();
    private DashboardAdapter adapter;
    private MyCache myCache;
    private String filter = "all";
    private ImageView backgroundImage;
    private ArrayList<Button> filterButtons = new ArrayList<Button>();
    private int filterButtonOnPosition = 0; // 필터 버튼들 중 On 되어있는 것의 위치 (버튼을 누르고 스크롤하여 Touch가 Cancel되었을 때 색을 제대로 표시하기 위해 쓰임)
    private boolean setBackground = false;
    private ImageButton preferenceBtn;
    private String[] preferencOption;

    private DrawerLayout drawerLayout;
    private LinearLayout drawerOpen;

    private boolean backAgainToFinish = false;

    // onCreate에서는 아직 화면이 뿌려지지 않은 상태이므로 getWidth 등이 0을 반환한다.  배경화면을 나타내기 위해 이 메소드를 Override
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!setBackground) {
            setBackground = true;
            int[] backgrounds = {R.drawable.background_1, R.drawable.background_2, R.drawable.background_3};
            backgroundImage = (ImageView)findViewById(R.id.main_background_image);
            Bitmap original = BitmapFactory.decodeResource(getResources(), backgrounds[(int)(Math.random()*backgrounds.length)]);
            int screenWidth = backgroundImage.getWidth(), screenHeight = backgroundImage.getHeight();
            System.out.println("이미지사이즈 " + backgroundImage.getWidth() + " " + backgroundImage.getHeight());

            Bitmap scaled;
            if ((double)original.getWidth() / (double)original.getHeight() > (double)screenWidth / (double)screenHeight) {
                int width = (int)((double)original.getHeight() * (double)screenWidth / (double)screenHeight);
                scaled = Bitmap.createBitmap(original, ((original.getWidth()) - (width))/2, 0, width, original.getHeight());
            }
            else {
                int height = (int)((double)original.getWidth() * (double)screenHeight / (double)screenWidth);
                scaled = Bitmap.createBitmap(original, height, ((original.getHeight()) - (height))/2, 0, original.getHeight());
            }
            backgroundImage.setImageBitmap(scaled);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_main_page);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("최신 컨텐츠를 내려받고 있습니다.");

        myCache = new MyCache(this);

        drawerLayout = (DrawerLayout)findViewById(R.id.content_list_drawer);
        drawerOpen = (LinearLayout)findViewById(R.id.drawer_open);
        drawerOpen.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        drawerLayout.openDrawer(Gravity.LEFT);
                        break;
                }
                return false;
            }
        });

        preferencOption = new String[]{("학교(" + myCache.getMySchoolName() +")변경"), "문의·오류제보", "만든 사람"};
        preferenceBtn = (ImageButton)findViewById(R.id.main_page_preference_button);
        preferenceBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP)
                    new AlertDialog.Builder(MainPage.this)
                            .setItems(preferencOption, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    preference(i);
                                }
                            }).show();
                return false;
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.dashboard_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrolling = false;
                        break;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (((visibleItemCount + firstVisibleItem) >= totalItemCount) && !downloading && !downloadedAllInServer) {
                    downloading = true;
                    getMoreList();
                }
            }
        });

        filterButtons.add((Button)findViewById(R.id.dashboard_all));
        filterButtons.add((Button)findViewById(R.id.dashboard_advertise));
        filterButtons.add((Button)findViewById(R.id.dashboard_lecture));
        filterButtons.add((Button)findViewById(R.id.dashboard_delivery));
        filterButtons.add((Button)findViewById(R.id.dashboard_restaurant));
        filterButtons.add((Button)findViewById(R.id.dashboard_custom));

        for (final Button btn : filterButtons) {
            btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (scrolling)
                        return false;

                    recyclerView.scrollToPosition(0);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (filterButtonOnPosition != filterButtons.indexOf(btn))
                                btn.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            if (filterButtonOnPosition != filterButtons.indexOf(btn))
                                btn.setTextColor(new WicamColors().TEXT_VERY_LIGHT);
                            break;
                        case MotionEvent.ACTION_UP:
                            filterButtonOnPosition = filterButtons.indexOf(btn);
                            for (Button grBtn : filterButtons) {
                                if (grBtn != btn)
                                    grBtn.setTextColor(new WicamColors().TEXT_VERY_LIGHT);
                            }
                            try {
                                filter = new UTFConvert().convert(btn.getTag().toString());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            getNewList("");
                            break;
                    }
                    return false;
                }
            });
        }

        contentDrawer = new ContentDrawer(this);

        getNewList("");
    }

    @Override
    public void getMoreList() {
        page++;
        getList("");
    }

    public void getNewList(String itemId) {

        progressDialog.show();
        downloadedAllInServer = false;
        page = 0;
        dashboardList.removeAll(dashboardList);

        getList(itemId);
    }

    public void getList(String itemId) {
        new DashboardAsyncTask(this, dashboardList).execute(new Security().WEB_ADDRESS + "dashboard_list.php?page=" + String.valueOf(page) + "&school_id=" + myCache.getMySchoolId() + "&filter=" + filter);
    }

    public void clearSharedPreferences() {
        getSharedPreferences("wicam", MODE_PRIVATE).edit().clear().commit();
    }

    @Override
    public void showDownloadedList() {
        progressDialog.dismiss();
        if (adapter == null) {
            adapter = new DashboardAdapter(this, dashboardList);
            recyclerView.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();
    }

    @Override
    public void updateAnItem(int position) {
        adapter.notifyItemChanged(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Singleton.create().isRefreshDashboard()) {
            Singleton.create().setRefreshDashboard(false);
            getNewList("");
        }

        if (resultCode == 1) { // 컨텐츠를 추가하고 돌아올 때
            contentDrawer.afterContentAdd = true;
            contentDrawer.getNewList();
        }
        else if (resultCode == 2) { // 컨텐츠를 삭제하고 돌아올 때
        }
    }

    public void intoContent(DashboardData dashboardData) {

        myCache.setContentId(dashboardData.getContent_idx());
        Singleton.setItemId(dashboardData.getIdx());

        switch (dashboardData.getContent()) {
            case "advertise":
                myCache.setDefaultCode("5");
                startActivityForResult(new Intent(this, AdvertiseListActivity.class), 0);
                break;
            case "lecture":
                myCache.setDefaultCode("3");
                startActivityForResult(new Intent(this, LectureListActivity.class), 0);
                break;
            case "delivery":
                myCache.setDefaultCode("1");
                startActivityForResult(new Intent(this, DeliveryListActivity.class), 0);
                break;
            case "restaurant":
                myCache.setDefaultCode("2");
                startActivityForResult(new Intent(this, RestaurantListActivity.class), 0);
                break;
            case "custom":
                myCache.setDefaultCode("0");
                startActivityForResult(new Intent(this, CustomListActivity.class), 0);
                break;
        }
    }

    public void preference(int index) {

        switch (index) {
            case 0:
                myCache.clearSchool();
                startActivity(new Intent(this, SelectSchoolActivity.class));
                finish();
                break;
            case 1:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"junseol86@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "위캠 관련 문의");
                intent.putExtra(Intent.EXTRA_TEXT, "폰 기종:" + Build.MODEL + "\n\n");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "이메일 앱을 선택하세요."));
                break;
            case 2:
                new AlertDialog.Builder(this)
                        .setTitle("만든 사람")
                        .setMessage("만든이(기획 & 개발 & 디자인):\n 고현민 (한동대 전산전자공학부 10)\n\n소속: 아카데브\n (한동대 소프트웨어 제작 동아리)" +
                                "\n\n도운이(한동대 데이터 입력):\n 조한나 (한동대 생명과학부 12)")
                        .setPositiveButton("만든이 Facebook", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                String url = "https://www.facebook.com/junseol86";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }).show();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        if (backAgainToFinish) {
            finish();
        }
        else {
            backAgainToFinish = true;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    backAgainToFinish = false;
                }
            }, 2000L);
        }
    }

}

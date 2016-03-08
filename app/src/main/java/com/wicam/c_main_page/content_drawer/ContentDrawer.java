package com.wicam.c_main_page.content_drawer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wicam.R;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.account_related.NickNameNeededTask;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.c_content_add.ContentAddActivity;
import com.wicam.c_content_add.ContentDeleteAsyncTask;
import com.wicam.c_main_page.MainPage;
import com.wicam.d_default_advertise.list_page.AdvertiseListActivity;
import com.wicam.d_default_custom.list_page.CustomListActivity;
import com.wicam.d_default_delivery.list_page.DeliveryListActivity;
import com.wicam.d_default_handong_bus.HandongBus;
import com.wicam.d_default_lecture.LectureListActivity;
import com.wicam.d_default_phonebook.list_page.PhonebookListActivity;
import com.wicam.d_default_restaurant.list_page.RestaurantListActivity;
import com.wicam.d_web_view.WebPage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Hyeonmin on 2015-07-13.
 */
public class ContentDrawer {

    private MainPage mainPage;
    private RecyclerView contentRecyclerView;
    private LinearLayoutManager layoutManager;
    private ContentListAdapter contentListAdapter;
    private ArrayList<ContentListData> contentList = new ArrayList<ContentListData>();
    private ArrayList<ContentListData> filteredContentList = new ArrayList<ContentListData>();
    private DrawerLayout contentListDrawer;
    private ImageButton favoriteUpButton;
    private Button recentButton, famousButton;
    private LinearLayout contentAdd;
    public boolean afterContentAdd = false;
    private EditText contentEditText;
    private ImageButton contentSearchClear;

    private boolean favoriteFirst = true;
    private int recentOrFamousFirst = 0;
    private MyCache myCache;
    private ContentListData content;

    private int page;
    private ProgressDialog progressDialog;
    private boolean downloading = false; // 항목을 다운로드하고 있는지 여부 (스크롤 끝에서 getMoreList가 여러번 실행되지 않도록)
    private boolean downloadedAllInServer = false; // 모든 항목을 다 다운로드하여, 스크롤 끝에서도 더 이상 getMoreList를 실행하지 않아도 되는지 여부
    private boolean scrolling = false;

    public ContentDrawer(final MainPage mainPage) {
        this.mainPage = mainPage;
        progressDialog = new ProgressDialog(mainPage);
        progressDialog.setTitle("컨텐츠를 내려받고 있습니다.");

        myCache = new MyCache(mainPage);

        contentRecyclerView = (RecyclerView)mainPage.findViewById(R.id.content_recycler_view);
        contentRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mainPage);
        contentRecyclerView.setLayoutManager(layoutManager);

        contentRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        ((RelativeLayout) mainPage.findViewById(R.id.long_click_message)).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        ((RelativeLayout) mainPage.findViewById(R.id.long_click_message)).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrolling = false;
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!scrolling)
                                    ((RelativeLayout) mainPage.findViewById(R.id.long_click_message)).setVisibility(View.VISIBLE);
                            }
                        }, 500L);
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

        contentListDrawer = (DrawerLayout)mainPage.findViewById(R.id.content_list_drawer);

        contentEditText = (EditText)mainPage.findViewById(R.id.content_edit_text);
        contentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                    getNewList();
                return false;
            }
        });

        contentSearchClear = (ImageButton)mainPage.findViewById(R.id.content_search_clear);
        contentSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contentEditText.setText("");
                getNewList();
            }
        });

        favoriteUpButton = (ImageButton)mainPage.findViewById(R.id.content_list_favorite_up_button);
        favoriteUpButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                contentEditText.setText("");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((RelativeLayout)mainPage.findViewById(R.id.content_list_favorite_up_rl)).setBackgroundColor(new WicamColors().WC_DARK_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        favoriteFirst = !favoriteFirst;
                        ((RelativeLayout)mainPage.findViewById(R.id.content_list_favorite_up_rl)).setBackgroundColor(favoriteFirst ? new WicamColors().WC_BLUE : new WicamColors().WC_HALF_BLUE);
                        getNewList();
                        break;
                }
                return false;
            }
        });

        recentButton = (Button)mainPage.findViewById(R.id.content_list_recent_button);
        recentButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                contentEditText.setText("");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        recentButton.setBackgroundColor(new WicamColors().WC_DARK_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        recentButton.setBackgroundColor(new WicamColors().WC_BLUE);
                        famousButton.setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        recentOrFamousFirst = 0;
                        ((RelativeLayout)mainPage.findViewById(R.id.content_list_favorite_up_rl)).setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        favoriteFirst = false;
                        getNewList();
                        break;
                }
                return false;
            }
        });

        famousButton = (Button)mainPage.findViewById(R.id.content_list_famous_button);
        famousButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                contentEditText.setText("");
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        famousButton.setBackgroundColor(new WicamColors().WC_DARK_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        famousButton.setBackgroundColor(new WicamColors().WC_BLUE);
                        recentButton.setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        recentOrFamousFirst = 1;
                        ((RelativeLayout)mainPage.findViewById(R.id.content_list_favorite_up_rl)).setBackgroundColor(new WicamColors().WC_HALF_BLUE);
                        favoriteFirst = false;
                        getNewList();
                        break;
                }
                return false;
            }
        });

        contentAdd = (LinearLayout)mainPage.findViewById(R.id.content_add);
        contentAdd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        contentAdd.setBackgroundColor(new WicamColors().WC_DARK_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        contentAdd.setBackgroundColor(new WicamColors().WC_BLUE);
                        Singleton.create().setAddOrModify(0);
                        new NickNameNeededTask(mainPage, 0);
                        break;
                }
                return false;
            }
        });

        getNewList();
    }

    public void getNewList() {

        if (afterContentAdd) { // 컨텐츠를 추가하고 돌아오면 즐겨찾기를 해제하고 최신순으로 보여준다

            contentEditText.setText("");

            afterContentAdd = false;
            recentButton.setBackgroundColor(new WicamColors().WC_BLUE);
            famousButton.setBackgroundColor(new WicamColors().WC_HALF_BLUE);
            recentOrFamousFirst = 0;
            ((RelativeLayout)mainPage.findViewById(R.id.content_list_favorite_up_rl)).setBackgroundColor(new WicamColors().WC_HALF_BLUE);
            favoriteFirst = false;

            new AlertDialog.Builder(mainPage)
                    .setMessage("컨텐츠가 업로드되었습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }

        progressDialog.show();
        downloadedAllInServer = false;
        page = 0;
        contentList.removeAll(contentList);

        getList();
    }

    public void getMoreList() {
        page++;
        getList();
    }

    public void getList() {

        String keyword = "";

        try {
            keyword = new UTFConvert().convert(contentEditText.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new ContentListAsyncTask(this, contentList).execute(new Security().WEB_ADDRESS + "content_list_paged.php?school_id=" + new MyCache(mainPage).getMySchoolId()
                        + "&user_id=" + new MyCache(mainPage).getMyId() + "&page=" + String.valueOf(page)
                        + "&favorite=" + (favoriteFirst ? "1" : "0") + "&recent_or_famous=" + String.valueOf(recentOrFamousFirst)
                        + "&keyword=" + keyword
        );
    }


    public void contentListDownResult() { // 다운받은 목록을 또다른 ArrayList에 복사 (키워드검색을 위함)
        progressDialog.dismiss();

        if (!filteredContentList.isEmpty())
            filteredContentList.removeAll(filteredContentList);
        for (ContentListData cd : contentList) {
            filteredContentList.add(cd);
        }


        showContentList();
    }

    public void setFavoriteUpFalse() { // 컨텐츠의 즐겨찾기를 on/off 시키면 바로 정렬되는 것이 아니므로 favoriteUP을 false로 만들어준다.
        favoriteFirst = false;
        ((RelativeLayout)mainPage.findViewById(R.id.content_list_favorite_up_rl)).setBackgroundColor(new WicamColors().WC_HALF_BLUE);
    }


    public void showContentList() {

        if (favoriteFirst) { // 즐겨찾기를 먼저 보여주기로 세팅된 경우
            Collections.sort(filteredContentList, new Comparator<ContentListData>() {
                @Override
                public int compare(ContentListData content1, ContentListData content2) {
                    String cf1 = String.valueOf(content1.getFavorite());
                    String cf2 = String.valueOf(content2.getFavorite());

                    if (cf1.equalsIgnoreCase("1") && cf2.equalsIgnoreCase("1")) { // 둘 다 즐겨찾기되어있을 경우 나의 조회수가 높은 순으로
                        int ct1 = mainPage.getSharedPreferences("wicam", Context.MODE_PRIVATE).getInt("my_views_" + String.valueOf(content1.getContent_id()), 0);
                        int ct2 = mainPage.getSharedPreferences("wicam", Context.MODE_PRIVATE).getInt("my_views_" + String.valueOf(content2.getContent_id()), 0);

                        return ct2 - ct1;
                    }
                    else
                        return contentList.indexOf(content1) - contentList.indexOf(content2);
                }
            });

            Toast.makeText(mainPage, "즐겨찾기된 항목끼리는 사용빈도로 정렬됩니다.", Toast.LENGTH_SHORT).show();
        }

        if (contentListAdapter == null) {
            contentListAdapter = new ContentListAdapter(this, filteredContentList);
            contentRecyclerView.setAdapter(contentListAdapter);
        }
        else
        contentListAdapter.notifyDataSetChanged();

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                contentListDrawer.openDrawer(Gravity.LEFT);
            }
        }, 1000L);
    }

    public MainPage getMainPage() {
        return mainPage;
    }

    public void onItemClick(int position) { // 컨텐츠를 클릭하면 해당 컨텐츠의 내용을 받아오는 AsyncTask 가동

        int myViews = mainPage.getSharedPreferences("wicam", Context.MODE_PRIVATE).getInt("my_views_" + String.valueOf(filteredContentList.get(position).getContent_id()), 0);
        mainPage.getSharedPreferences("wicam", Context.MODE_PRIVATE).edit().putInt("my_views_" + String.valueOf(filteredContentList.get(position).getContent_id()), myViews + 1).commit();

        try {
            intoContent(filteredContentList.get(position));
        }
        catch (Exception e) {};
    }

    public void onItemLoinClick(final int position) {

        AlertDialog.Builder dialogBuilder;

        String dialogMessage = "";

        dialogMessage += "작성자: " + filteredContentList.get(position).getModifier_nickname();
        dialogMessage += "\n작성일: " + filteredContentList.get(position).getModify_time();

        String schools = "";
        for (String school: filteredContentList.get(position).getSchoolNameList()) {
            schools += school;
            if (filteredContentList.get(position).getSchoolNameList().indexOf(school) < filteredContentList.get(position).getSchoolNameList().size() - 1)
                schools += ", ";
        }

        dialogMessage += "\n사용학교: " + schools;

        if (filteredContentList.get(position).getContent_type().equalsIgnoreCase("2"))
            dialogMessage += "\nURL: " + filteredContentList.get(position).getUrl_link();

        dialogMessage += "\n\n" + filteredContentList.get(position).getDescription();

        if (!filteredContentList.get(position).getContact().trim().equalsIgnoreCase(""))
            dialogMessage += "\n\n작성자 연락처: " + filteredContentList.get(position).getContact();

        dialogBuilder = new AlertDialog.Builder(mainPage)
                .setTitle(filteredContentList.get(position).getContent_name())
                .setMessage(dialogMessage);

        if (filteredContentList.get(position).getWriter_id().equalsIgnoreCase(myCache.getMyId()))
            dialogBuilder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new AlertDialog.Builder(mainPage)
                            .setMessage("컨텐츠를 정말로 삭제하시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ContentListData content = filteredContentList.get(position);

                                    new ContentDeleteAsyncTask(ContentDrawer.this).execute(new Security().WEB_ADDRESS + "delete_content.php?user_id=" + myCache.getMyId() + "&device_id=" + myCache.getDeviceId()
                                            + "&content_id=" + content.getContent_id() + "&default_code=" + content.getDefault_code() + "&content_type=" + content.getContent_type()
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
            });

        if (filteredContentList.get(position).getWriter_id().equalsIgnoreCase(myCache.getMyId()))
            dialogBuilder.setNegativeButton("수정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Singleton.create().setContentListData(filteredContentList.get(position));
                    Singleton.create().setAddOrModify(1);
                    mainPage.startActivityForResult(new Intent(mainPage, ContentAddActivity.class), 0);
                }
            });
        dialogBuilder.show();
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = mainPage.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public void intoContent(ContentListData content) throws PackageManager.NameNotFoundException {

        myCache.setDefaultCode(content.getDefault_code());
        myCache.setContentId(content.getContent_id());
        this.content = content;

        new ViewsUpAsyncTask(this).execute(new Security().WEB_ADDRESS + "views_up.php?user_id=" + myCache.getMyId() +"&device_id=" + myCache.getDeviceId()
                + "&school_id=" + myCache.getMySchoolId() + "&default_code=" + myCache.getDefaultCode() + "&content_id=" + myCache.getContentId()
        );

    }

    public void intoContentAfterViewUp() {

        if (content.getDefault_code().equalsIgnoreCase("0")) { // 기본 컨텐츠가 아닐 시
            switch (content.getContent_type()) {
                case "1": // 목록형 컨텐츠일 시
                    mainPage.startActivityForResult(new Intent(mainPage, CustomListActivity.class), 0);
                    break;
                case "2": // 링크일 시
                    mainPage.startActivityForResult(new Intent(mainPage, WebPage.class).putExtra("url_link", content.getUrl_link()), 0);
                    break;
                case "3": // 앱일 시
                    final String packageName = content.getPackage_name();
                    if (isAppInstalled(packageName)){
                        Intent launchIntent = mainPage.getPackageManager().getLaunchIntentForPackage(packageName);
                        mainPage.startActivity(launchIntent);
                    }
                    else {
                        new AlertDialog.Builder(mainPage)
                                .setMessage("이 컨텐츠는 앱으로서, 다운로드 및 설치를 필요로 합니다.  Play스토어로 이동하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("market://details?id="+ packageName));
                                        mainPage.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    }
                    break;
            }

        }
        else { // 기본 컨텐츠일 시
            switch (content.getDefault_code()) {
                case "1":
                    mainPage.startActivityForResult(new Intent(mainPage, DeliveryListActivity.class), 0);
                    break;
                case "2":
                    mainPage.startActivityForResult(new Intent(mainPage, RestaurantListActivity.class), 0);
                    break;
                case "3":
                    mainPage.startActivityForResult(new Intent(mainPage, LectureListActivity.class), 0);
                    break;
                case "4":
                    mainPage.startActivityForResult(new Intent(mainPage, PhonebookListActivity.class), 0);
                    break;
                case "5":
                    mainPage.startActivityForResult(new Intent(mainPage, AdvertiseListActivity.class), 0);
                    break;
                case "7":
                    final String packageName = "com.hungry_handongi";
                    if (isAppInstalled(packageName)){
                        Intent launchIntent = mainPage.getPackageManager().getLaunchIntentForPackage(packageName);
                        mainPage.startActivity(launchIntent);
                    }
                    else {
                        new AlertDialog.Builder(mainPage)
                                .setMessage("이 컨텐츠는 앱으로서, 다운로드 및 설치를 필요로 합니다.  Play스토어로 이동하시겠습니까?")
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("market://details?id=" + packageName));
                                        mainPage.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                })
                                .show();
                    }
                    break;
                case "8":
                    mainPage.startActivityForResult(new Intent(mainPage, HandongBus.class), 0);
                    break;
            }
        }

    }

    public void refreshAnItem(int position) {
        contentListAdapter.notifyItemChanged(position);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public boolean isDownloadedAllInServer() {
        return downloadedAllInServer;
    }

    public void setDownloadedAllInServer(boolean downloadedAllInServer) {
        this.downloadedAllInServer = downloadedAllInServer;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }
}

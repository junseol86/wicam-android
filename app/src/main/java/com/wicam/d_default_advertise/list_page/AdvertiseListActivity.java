package com.wicam.d_default_advertise.list_page;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.NickNameNeededTask;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;
import com.wicam.a_common_utils.account_related.item_detail_comment.ListActivityShowingLikesAndComments;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_advertise.detail_page.AdvertiseDetailActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-16.
 */
public class AdvertiseListActivity extends ListActivityShowingLikesAndComments {
    private String kind = "";
    private String keyword = "";

    private AdvertiseListAdapter advertiseListAdapter;
    private ArrayList<ItemData> advertiseList = new ArrayList<ItemData>();

    private ImageButton seeFavoriteBtn;
    private ArrayList<Button> includesButton = new ArrayList<Button>();
    private int includesButtonOnPosition = 0; // 장르 버튼들 중 On 되어있는 것의 위치 (버튼을 누르고 스크롤하여 Touch가 Cancel되었을 때 색을 제대로 표시하기 위해 쓰임)

    private Button addadvertiseButton;
    private boolean showingOnlyOne = false;
    private Button seeAllAgainButton; // 아이템 추가 혹은 수정 후 해당 식당만 나타나는 화면에서 다시 전체보기로 전환하는 버튼
    private TextView addadvertiseTextView;

    private EditText editText;

    private boolean seeFavoriteOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_advertise_list);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오고 있습니다.");

        recyclerView = (RecyclerView)findViewById(R.id.advertise_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        ((LinearLayout) findViewById(R.id.advertise_list_add_advertise_ll)).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        ((LinearLayout) findViewById(R.id.advertise_list_add_advertise_ll)).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrolling = false;
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!scrolling)
                                    ((LinearLayout) findViewById(R.id.advertise_list_add_advertise_ll)).setVisibility(View.VISIBLE);
                            }
                        }, 300L);
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (((visibleItemCount + firstVisibleItem) >= totalItemCount) && !downloading && !downloadedAllInServer && !showingOnlyOne) {
                    downloading = true;
                    getMoreList();
                }
            }
        });

        seeFavoriteBtn = (ImageButton)findViewById(R.id.advertise_list_see_favorite_button);
        seeFavoriteBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (scrolling)
                    return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        seeFavoriteBtn.setBackgroundResource(seeFavoriteOn ? R.drawable.item_list_see_favorite_off : R.drawable.item_list_see_favorite_on);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        seeFavoriteBtn.setBackgroundResource(!seeFavoriteOn ? R.drawable.item_list_see_favorite_off : R.drawable.item_list_see_favorite_on);
                        break;
                    case MotionEvent.ACTION_UP:
                        seeFavoriteOn = !seeFavoriteOn;
                        includesButtonOnPosition = 0;
                        for (Button grBtn : includesButton) {
                            grBtn.setTextColor(includesButton.indexOf(grBtn) == 0 ? new WicamColors().WC_BLUE : new WicamColors().TEXT_VERY_LIGHT);
                        }
                        kind = "";
                        keyword = "";
                        getNewList("");
                        break;
                }
                return false;
            }
        });

        includesButton.add((Button) findViewById(R.id.advertise_all));
        includesButton.add((Button) findViewById(R.id.advertise_news));
        includesButton.add((Button) findViewById(R.id.advertise_recruit));
        includesButton.add((Button) findViewById(R.id.advertise_contest));
        includesButton.add((Button) findViewById(R.id.advertise_career));
        includesButton.add((Button) findViewById(R.id.advertise_show));
        includesButton.add((Button) findViewById(R.id.advertise_employ));
        includesButton.add((Button) findViewById(R.id.advertise_others));

        for (final Button includesBtn : includesButton) {
            includesBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (scrolling)
                        return false;

                    recyclerView.scrollToPosition(0);

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (includesButtonOnPosition != includesButton.indexOf(includesBtn))
                                includesBtn.setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            if (includesButtonOnPosition != includesButton.indexOf(includesBtn))
                                includesBtn.setTextColor(new WicamColors().TEXT_VERY_LIGHT);
                            break;
                        case MotionEvent.ACTION_UP:
                            includesButtonOnPosition = includesButton.indexOf(includesBtn);
                            for (Button grBtn : includesButton) {
                                if (grBtn != includesBtn)
                                    grBtn.setTextColor(new WicamColors().TEXT_VERY_LIGHT);
                            }
                            try {
                                kind = new UTFConvert().convert(includesBtn.getTag().toString());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            keyword = "";
                            seeFavoriteBtn.setBackgroundResource(R.drawable.item_list_see_favorite_off);
                            seeFavoriteOn = false;

                            getNewList("");
                            break;
                    }
                    return false;
                }
            });
        }

        editText = (EditText)findViewById(R.id.advertise_list_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((TextView) findViewById(R.id.advertise_list_edit_text_description)).setVisibility(String.valueOf(s).trim().equalsIgnoreCase("") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER && !editText.getText().toString().trim().equalsIgnoreCase("")) {
                    try {
                        keyword = new UTFConvert().convert(editText.getText().toString().trim());
                        kind = "";
                        for (Button grBtn : includesButton) {
                            grBtn.setTextColor(includesButton.indexOf(grBtn) == 0 ? new WicamColors().WC_BLUE : new WicamColors().TEXT_VERY_LIGHT);
                        }
                        includesButtonOnPosition = 0;
                        seeFavoriteBtn.setBackgroundResource(R.drawable.item_list_see_favorite_off);
                        seeFavoriteOn = false;

                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                        getNewList("");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });

        addadvertiseButton = (Button)findViewById(R.id.add_advertise_button);
        addadvertiseTextView = (TextView)findViewById(R.id.add_advertise_button_text);
        addadvertiseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        addadvertiseTextView.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        addadvertiseTextView.setTextColor(new WicamColors().TEXT_NORMAL);
                        Singleton.create().setAddOrModify(0);
                        Singleton.create().setItemDataList(advertiseList);
                        new NickNameNeededTask(AdvertiseListActivity.this, 1);
                        break;
                }
                return false;
            }
        });

        seeAllAgainButton = (Button)findViewById(R.id.advertise_see_all_again_button);
        seeAllAgainButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        seeAllAgainButton.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        seeAllAgainButton.setTextColor(new WicamColors().TEXT_NORMAL);
                        seeAllAgainButton.setVisibility(View.GONE);
                        showingOnlyOne = false;
                        ((LinearLayout)findViewById(R.id.advertise_top_bar)).setVisibility(View.VISIBLE);
                        getNewList("");
                        break;
                }
                return false;
            }
        });

        getNewList(Singleton.create().getItemId());
    }

    @Override
    public void getNewList(String itemId) {

        if (!itemId.equalsIgnoreCase("")) {
            seeAllAgainButton.setVisibility(View.VISIBLE); // 식당 하나만 보여주기 모드일 때는 이로부터 되돌아오는 버튼을 삽입
            ((LinearLayout)findViewById(R.id.advertise_top_bar)).setVisibility(View.GONE);
        }

        progressDialog.show();
        downloadedAllInServer = false;
        page = 0;
        advertiseList.removeAll(advertiseList);

        getList(itemId);
    }

    @Override
    public void getList(String itemId) {
        new AdvertiseListAsyncTask(this, advertiseList).execute(new Security().WEB_ADDRESS + "advertise_list.php?page=" + String.valueOf(page) + "&kind=" + kind + "&keyword=" + keyword +
                "&school_id=" + new MyCache(this).getMySchoolId()
                + "&device_id=" + new MyCache(this).getDeviceId()
                + "&user_id=" + new MyCache(this).getMyId() + "&see_favorite=" + (seeFavoriteOn ? "1" : "0") + "&item_id=" + itemId);
//                 item_id는 새로추가나 수정 등의 작업 이후 한 해당 아이템만 보여줄 때 사용
    }


    @Override
    public void updateAnItem(int position) {
        advertiseListAdapter.notifyItemChanged(position);
    }

    @Override
    public void showDownloadedList() {

        if (!Singleton.getItemId().equalsIgnoreCase("")) {
            Singleton.create().setItemPosition(0);
            Singleton.create().setItemDataList(advertiseList);
            //-------------------------------------------------//
            Intent intent = new Intent(this, AdvertiseDetailActivity.class);
            startActivityForResult(intent, 0);
        }

        progressDialog.dismiss();
        if (page == 0) {
            advertiseListAdapter = new AdvertiseListAdapter(this, advertiseList);
            recyclerView.setAdapter(advertiseListAdapter);
        }
        else
            advertiseListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!Singleton.create().getItemId().equalsIgnoreCase("")) {
            Singleton.create().setItemId("");
            finish();
        }

        if (resultCode == 0) { // 상세 페이지에서 돌아올 때 (즐겨찾기, 좋아요 등을 refresh)
            advertiseListAdapter.notifyItemChanged(Singleton.create().getItemPosition());
        }
        else if (resultCode == 1) { // 아이템을 추가하거나 변경하고 돌아올 때
            showingOnlyOne = true;
            String itemId = data.getStringExtra("itemId");
            getNewList(itemId);
        }
        else if (resultCode == 2) { // 아이템을 삭제하고 돌아올 때
            getNewList("");
        }
    }
}

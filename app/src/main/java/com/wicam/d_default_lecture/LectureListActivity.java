package com.wicam.d_default_lecture;

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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-16.
 */
public class LectureListActivity extends ListActivityShowingLikesAndComments {
    private String keyword = "";

    private LectureListAdapter lectureListAdapter;
    private ArrayList<ItemData> lectureList = new ArrayList<ItemData>();

    private Button addlectureButton, seeAllButton;
    private boolean showingOnlyOne = false;
    private Button seeAllAgainButton; // 식당 추가 혹은 수정 후 해당 식당만 나타나는 화면에서 다시 전체보기로 전환하는 버튼
    private TextView addlectureTextView;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_lecture_list);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오고 있습니다.");

        recyclerView = (RecyclerView)findViewById(R.id.lecture_list_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        ((LinearLayout) findViewById(R.id.lecture_list_add_lecture_ll)).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        ((LinearLayout) findViewById(R.id.lecture_list_add_lecture_ll)).setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrolling = false;
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!scrolling)
                                    ((LinearLayout) findViewById(R.id.lecture_list_add_lecture_ll)).setVisibility(View.VISIBLE);
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


        editText = (EditText)findViewById(R.id.lecture_list_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((TextView) findViewById(R.id.lecture_list_edit_text_description)).setVisibility(String.valueOf(s).trim().equalsIgnoreCase("") ? View.VISIBLE : View.GONE);
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

        seeAllButton = (Button)findViewById(R.id.see_all_btn);
        seeAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = "";
                getNewList("");
            }
        });

        addlectureButton = (Button)findViewById(R.id.add_lecture_button);
        addlectureTextView = (TextView)findViewById(R.id.add_lecture_button_text);
        addlectureButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        addlectureTextView.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        addlectureTextView.setTextColor(new WicamColors().TEXT_NORMAL);
                        startActivityForResult(new Intent(LectureListActivity.this, LectureAddActivity.class), 0);
                        break;
                }
                return false;
            }
        });

        seeAllAgainButton = (Button)findViewById(R.id.lecture_see_all_again_button);
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
                        ((LinearLayout)findViewById(R.id.lecture_top_bar)).setVisibility(View.VISIBLE);
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
            ((LinearLayout)findViewById(R.id.lecture_top_bar)).setVisibility(View.GONE);
        }

        progressDialog.show();
        downloadedAllInServer = false;
        page = 0;
        lectureList.removeAll(lectureList);

        getList(itemId);
    }

    @Override
    public void getList(String itemId) {
        new LectureListAsyncTask(this, lectureList).execute(new Security().WEB_ADDRESS + "lecture_list.php?page=" + String.valueOf(page) + "&keyword=" + keyword +
                "&school_id=" + new MyCache(this).getMySchoolId()
                + "&device_id=" + new MyCache(this).getDeviceId()
                + "&user_id=" + new MyCache(this).getMyId() + "&item_id=" + itemId);
//                 item_id는 새로추가나 수정 등의 작업 이후 한 해당 아이템만 보여줄 때 사용
    }


    @Override
    public void updateAnItem(int position) {
        lectureListAdapter.notifyItemChanged(position);
    }

    @Override
    public void showDownloadedList() {
        progressDialog.dismiss();

        if (!Singleton.getItemId().equalsIgnoreCase("")) {
            Singleton.create().setItemPosition(0);
            Singleton.create().setItemDataList(lectureList);
            //-------------------------------------------------//
            Intent intent = new Intent(this, LectureDetailActivity.class);
            startActivityForResult(intent, 0);
        }

        if (page == 0) {
            lectureListAdapter = new LectureListAdapter(this, lectureList);
            recyclerView.setAdapter(lectureListAdapter);
        }
        else
            lectureListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!Singleton.create().getItemId().equalsIgnoreCase("")) {
            Singleton.create().setItemId("");
            finish();
        }

        if (resultCode == 0) { // 상세 페이지에서 돌아올 때 (즐겨찾기, 좋아요 등을 refresh)
            lectureListAdapter.notifyItemChanged(Singleton.create().getItemPosition());
        }
        else if (resultCode == 1) { // 업체를 추가하거나 변경하고 돌아올 때
            showingOnlyOne = true;
            String itemId = data.getStringExtra("itemId");
            getNewList(itemId);
        }
        else if (resultCode == 2) { // 아이템을 삭제하고 돌아올 때
            getNewList("");
        }
    }
}

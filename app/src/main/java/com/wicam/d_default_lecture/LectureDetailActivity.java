package com.wicam.d_default_lecture;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.comment_related.DetailActivityWithCommentAndReport;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-20.
 */
public class LectureDetailActivity extends DetailActivityWithCommentAndReport {
    private LectureDetailAdapter adapter;
    private LectureData lectureDetailData;
    private LectureDetailActivity lectureDetailActivity;
    private boolean scrolling = false;
    private LinearLayout showAssessInput, assessInput;
    private Button showAssessInputBtn;
    private boolean showingAssessInput = false;
    private int myDifficulty = 0, myInstructiveness = 0;
    private String myDescription = "";
    private ArrayList<ImageButton> myDifficultyList = new ArrayList<ImageButton>();
    private ArrayList<ImageButton> myInstructivenessList = new ArrayList<ImageButton>();
    private EditText descriptionEditText;
    private TextView descriptionTextCount;
    private Button assessUpload, assessModify, assessDelete;
    private LinearLayout assessModifyDelete;
    private boolean assessWorkStarted = false; // 평가 올리기/수정/삭제 버튼이 두 번 먹히지 않도록
    private MyCache myCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_lecture_detail);

        myCache = new MyCache(this);

        lectureDetailActivity = this;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 불러오고 있습니다.");

        recyclerView = (RecyclerView)findViewById(R.id.lecture_comments_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        showAssessInput.setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        showAssessInput.setVisibility(View.GONE);
                        scrolling = true;
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        scrolling = false;
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!scrolling)
                                    showAssessInput.setVisibility(View.VISIBLE);
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

                if (((visibleItemCount + firstVisibleItem) >= totalItemCount) && !downloading && !downloadedAllInServer && !noComment) {
                    downloading = true;
                    getMoreList();
                }
            }
        });

        showAssessInput = (LinearLayout)findViewById(R.id.show_assess_input);
        showAssessInputBtn = (Button)findViewById(R.id.show_assess_input_button);
        assessInput = (LinearLayout)findViewById(R.id.assess_input);

        showAssessInputBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        showAssessInputBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        showAssessInputBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        showAssessInputBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        showAssessToggle();
                        break;

                }
                return false;
            }
        });

        myDifficultyList.add((ImageButton)findViewById(R.id.difficulty_star_btn_1));
        myDifficultyList.add((ImageButton)findViewById(R.id.difficulty_star_btn_2));
        myDifficultyList.add((ImageButton)findViewById(R.id.difficulty_star_btn_3));
        myDifficultyList.add((ImageButton)findViewById(R.id.difficulty_star_btn_4));
        myDifficultyList.add((ImageButton)findViewById(R.id.difficulty_star_btn_5));

        myInstructivenessList.add((ImageButton)findViewById(R.id.instructiveness_star_btn_1));
        myInstructivenessList.add((ImageButton)findViewById(R.id.instructiveness_star_btn_2));
        myInstructivenessList.add((ImageButton)findViewById(R.id.instructiveness_star_btn_3));
        myInstructivenessList.add((ImageButton)findViewById(R.id.instructiveness_star_btn_4));
        myInstructivenessList.add((ImageButton)findViewById(R.id.instructiveness_star_btn_5));

        for (final ImageButton ib : myDifficultyList) {
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myDifficulty == ((myDifficultyList.indexOf(ib) + 1) * 2) - 1)
                        myDifficulty = (myDifficultyList.indexOf(ib) + 1) * 2;
                    else
                        myDifficulty = ((myDifficultyList.indexOf(ib) + 1) * 2) - 1;
                    setMyStars();
                }
            });
        }

        for (final ImageButton ib : myInstructivenessList) {
            ib.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myInstructiveness == ((myInstructivenessList.indexOf(ib) + 1) * 2) - 1)
                        myInstructiveness = (myInstructivenessList.indexOf(ib) + 1) * 2;
                    else
                        myInstructiveness = ((myInstructivenessList.indexOf(ib) + 1) * 2) - 1;
                    setMyStars();
                }
            });
        }

        descriptionEditText = (EditText)findViewById(R.id.lecture_description_et);
        descriptionTextCount = (TextView)findViewById(R.id.lecture_description_count);
        new EditTextAndTextCount(descriptionEditText, descriptionTextCount, 300).setEditTextAndTextCount();

        assessUpload = (Button)findViewById(R.id.assess_upload_btn);
        assessUpload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        assessUpload.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        assessUpload.setTextColor(new WicamColors().WC_BLUE);

                        if (myDifficulty == 0 || myInstructiveness == 0) {
                            new AlertDialog.Builder(LectureDetailActivity.this)
                                    .setMessage("'어려움'과 '유익함'의 별점(1~10)을 입력하세요.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                            return false;
                        }

                        if (assessWorkStarted)
                            return false;
                        assessWorkStarted = true;

                        try {
                            myDescription = new UTFConvert().convert(descriptionEditText.getText().toString());
                        } catch (Exception excrition) {

                        }

                        new MyAssessAsyncTask(LectureDetailActivity.this).execute(new Security().WEB_ADDRESS + "write_lecture_assess.php?user_id=" + myCache.getMyId() + "&device_id=" + myCache.getDeviceId()
                                        + "&item_id=" + ((LectureData) itemData).getItemId() + "&difficulty=" + String.valueOf(myDifficulty) + "&instructiveness=" + String.valueOf(myInstructiveness)
                                        + "&description=" + myDescription
                        );
                        break;
                }
                return false;
            }
        });

        assessModify = (Button)findViewById(R.id.assess_modify_btn);
        assessModify.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        assessModify.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        assessModify.setTextColor(new WicamColors().WC_BLUE);

                        try {
                            myDescription = new UTFConvert().convert(descriptionEditText.getText().toString());
                        } catch (Exception excrition) {

                        }

                        new MyAssessAsyncTask(LectureDetailActivity.this).execute(new Security().WEB_ADDRESS + "modify_lecture_assess.php?user_id=" + myCache.getMyId() + "&device_id=" + myCache.getDeviceId()
                                        + "&item_id=" + ((LectureData) itemData).getItemId() + "&difficulty=" + String.valueOf(myDifficulty) + "&instructiveness=" + String.valueOf(myInstructiveness)
                                        + "&description=" + myDescription
                        );
                        break;
                }
                return false;
            }
        });

        assessDelete = (Button)findViewById(R.id.assess_delete_btn);
        assessDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        assessDelete.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        assessDelete.setTextColor(new WicamColors().TEXT_NORMAL);
                        new MyAssessAsyncTask(LectureDetailActivity.this).execute(new Security().WEB_ADDRESS + "delete_lecture_assess.php?user_id=" + myCache.getMyId() + "&device_id=" + myCache.getDeviceId()
                                        + "&item_id=" + ((LectureData) itemData).getItemId()
                        );
                        break;
                }

                return false;
            }
        });

        assessModifyDelete = (LinearLayout)findViewById(R.id.assess_modify_delete_ll);

        contentsRefresh();
    }

    public void setMyStars() {

        for (int i = 0; i < myDifficultyList.size(); i++) {
            if (myDifficulty >= (i + 1) * 2)
                myDifficultyList.get(i).setBackgroundResource(R.drawable.assess_star_full);
            else if (myDifficulty == ((i + 1) * 2) - 1)
                myDifficultyList.get(i).setBackgroundResource(R.drawable.assess_star_half);
            else
                myDifficultyList.get(i).setBackgroundResource(R.drawable.assess_star_empty);
        }

        for (int i = 0; i < myInstructivenessList.size(); i++) {
            if (myInstructiveness >= (i + 1) * 2)
                myInstructivenessList.get(i).setBackgroundResource(R.drawable.assess_star_full);
            else if (myInstructiveness == ((i + 1) * 2) - 1)
                myInstructivenessList.get(i).setBackgroundResource(R.drawable.assess_star_half);
            else
                myInstructivenessList.get(i).setBackgroundResource(R.drawable.assess_star_empty);
        }
    }

    public void showAssessToggle() {
        showingAssessInput = !showingAssessInput;
        showAssessInput.setVisibility(showingAssessInput ? View.GONE : View.VISIBLE);
        assessInput.setVisibility(!showingAssessInput ? View.GONE : View.VISIBLE);
    }

    @Override
    public void downloadComments() {
        new LectureAssessAsyncTask(this, itemAndCommentDataList).execute(new Security().WEB_ADDRESS + "lecture_assesses.php?page=" + String.valueOf(page)
                + "&item_id=" + lectureDetailData.getItemId() + "&user_id=" + new MyCache(LectureDetailActivity.this).getMyId());
    }

    @Override
    public void setPageContents() {
        lectureDetailData = (LectureData)itemData;
    }

    @Override
    public void updateAnItem(int position) {
        if (position == 0) {
            lectureDetailData = (LectureData) itemAndCommentDataList.get(0);
            setPageContents();
            return;
        }
        adapter.notifyItemChanged(position);
    }

    @Override
    public void showDownloadedList() {
        progressDialog.dismiss();

        if (page == 0) {
            adapter = new LectureDetailAdapter(lectureDetailActivity, this, itemAndCommentDataList);
            recyclerView.setAdapter(adapter);
        }
        else
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void afterItemModified(String lectureId) {
        Intent intent = new Intent().putExtra("itemId", lectureId);
        setResult(1, intent);
        finish();
    }

    public void afterLectureDeleted() {
        setResult(2);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (showingAssessInput) {
            showAssessToggle();
            return;
        }

        setResult(0);
        finish();
    }

    @Override
    public void contentsRefresh() {
        page = 0;
        downloadedAllInServer = false;
        progressDialog.show();
        if (itemAndCommentDataList != null)
            itemAndCommentDataList.removeAll(itemAndCommentDataList);
        itemData = Singleton.create().getItemDataList().get(Singleton.create().getItemPosition());

        // 내가 입력한 강의평가가 있다면 이에 따라 입력부분을 세팅해준다.
        myDifficulty = ((LectureData)itemData).getMyDifficulty();
        myInstructiveness = ((LectureData)itemData).getMyInstructiveness();
        setMyStars();
        myDescription = ((LectureData) itemData).getMyDescription();
        descriptionEditText.setText(myDescription);

        assessUpload.setVisibility((myDifficulty != 0 && myInstructiveness != 0) ? View.GONE : View.VISIBLE);
        assessModifyDelete.setVisibility(!(myDifficulty != 0 && myInstructiveness != 0) ? View.GONE : View.VISIBLE);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(descriptionEditText.getWindowToken(), 0);

        if (showingAssessInput)
            showAssessToggle();

        assessWorkStarted = false;

        itemAndCommentDataList.add(itemData);
        setPageContents();
        downloadComments();
    }

}

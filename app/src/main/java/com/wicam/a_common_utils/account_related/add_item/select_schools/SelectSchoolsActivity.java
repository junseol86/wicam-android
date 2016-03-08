package com.wicam.a_common_utils.account_related.add_item.select_schools;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-27.
 */
public class SelectSchoolsActivity extends Activity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SelectSchoolsAdapter adapter;
    private ProgressDialog progressDialog;

    private ArrayList<SchoolsIdNameData> downloadedSchoolList = new ArrayList<SchoolsIdNameData>();
    private ArrayList<String> selectedSchoolIdList;
    private Button selectDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_common_select_schools);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록을 다운받고 있습니다.");

        selectedSchoolIdList = Singleton.create().getSchoolIdList();
        recyclerView = (RecyclerView)findViewById(R.id.select_location_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        selectDone = (Button)findViewById(R.id.select_schools_ok_btn);
        selectDone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectDone.setBackgroundColor(new WicamColors().WC_DARK_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        selectDone.setBackgroundColor(new WicamColors().WC_BLUE);
                        if (selectedSchoolIdList.size() > 0) {
                            Intent intent = new Intent().putExtra("schoolNames", ((TextView)findViewById(R.id.select_schools_names)).getText().toString());
                            setResult(SelectSchoolsActivity.RESULT_OK, intent);
                            finish();
                        }
                        break;
                }
                return false;
            }
        });

        progressDialog.show();
        new SchoolsListAsyncTask(this).execute(new Security().WEB_ADDRESS + "school_list.php");
    }

    public void setSchoolText() {
        String schools = "";
        for (int i = 0; i < selectedSchoolIdList.size(); i++) {
            schools += findSchoolName(selectedSchoolIdList.get(i));
            if (i < selectedSchoolIdList.size() - 1)
                schools += ", ";
        }
        ((TextView)findViewById(R.id.select_schools_names)).setText(schools);
    }

    public void showDownloadedResult() {
        progressDialog.dismiss();
        adapter = new SelectSchoolsAdapter(this);
        recyclerView.setAdapter(adapter);
        setSchoolText();
    }

    // 리사이클러에서 학교를 선택할때마다 화면 반응
    public void selectRefresh() {
        setSchoolText();
        adapter.notifyDataSetChanged();
        ((TextView)findViewById(R.id.select_schools_count)).setText(String.valueOf(selectedSchoolIdList.size()) + "/10");
    }

    public ArrayList<SchoolsIdNameData> getDownloadedSchoolList() {
        return downloadedSchoolList;
    }

    public ArrayList<String> getSelectedSchoolIdList() {
        return selectedSchoolIdList;
    }

    public String findSchoolName(String id) { // 선택된 ID를 가진 학교의 이름을 반환
        String schoolName = "";
        for (SchoolsIdNameData snd : downloadedSchoolList) {
            if (snd.getSchoolId().equalsIgnoreCase(id)) {
                schoolName = snd.getSchoolName();
                break;
            }
        }
        return schoolName;
    }
}

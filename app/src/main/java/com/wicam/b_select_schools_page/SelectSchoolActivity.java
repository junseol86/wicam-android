package com.wicam.b_select_schools_page;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.wicam.R;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.c_main_page.MainPage;

import java.util.ArrayList;


public class SelectSchoolActivity extends Activity {

    private SelectSchoolActivity selectSchoolActivity;
    private ArrayList<SchoolData> schoolAll = new ArrayList<SchoolData>();
    private ArrayList<SchoolData> schoolFiltered = new ArrayList<SchoolData>();

    private RecyclerView schoolRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SchoolAdapter schoolAdapter;
    private EditText selectSchoolEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_select_school_page);

        selectSchoolActivity = this;

        schoolRecyclerView = (RecyclerView)findViewById(R.id.school_recycler_view);
        schoolRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        schoolRecyclerView.setLayoutManager(layoutManager);

        new SchoolListAsyncTask(this, schoolAll).execute((new Security()).WEB_ADDRESS + "school_list.php");

        selectSchoolEditText = (EditText)findViewById(R.id.select_school_edit_text);
        selectSchoolEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                schoolFiltered.removeAll(schoolFiltered);
                for (SchoolData sd : schoolAll) {
                    if (sd.getSchoolName().contains(selectSchoolEditText.getText().toString()))
                        schoolFiltered.add(sd);
                }
                schoolAdapter.notifyDataSetChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        selectSchoolEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER && schoolFiltered.size() > 0 && !selectSchoolEditText.getText().toString().trim().equalsIgnoreCase(""))
                    selectSchool(0);
                return false;
            }
        });
    }

    public void showDownloadedList() {
        for (SchoolData sd: schoolAll) {
            schoolFiltered.add(sd);
        }
        schoolAdapter = new SchoolAdapter(this, schoolFiltered);
        schoolRecyclerView.setAdapter(schoolAdapter);
    }

    public void selectSchool(final int position) {
        new AlertDialog.Builder(this)
                .setMessage("'" + schoolFiltered.get(position).getSchoolName() + "'가 맞으신가요?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new MyCache(selectSchoolActivity).setMySchoolId(schoolFiltered.get(position).getSchoolId());
                        new MyCache(selectSchoolActivity).setMySchoolName(schoolFiltered.get(position).getSchoolName());
                        startActivity(new Intent(SelectSchoolActivity.this, MainPage.class));
                        finish();
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

}

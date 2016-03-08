package com.wicam.d_default_lecture;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;

import java.io.UnsupportedEncodingException;

/**
 * Created by Hyeonmin on 2015-08-08.
 */
public class LectureAddActivity extends Activity {

    private MyCache myCache;
    private EditText lectureNameEditText, professorNameEditText, majorEditText;
    private TextView lectureNameTextCount, professorNameTextCount, majorTextCount;
    private Button addSubmitBtn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_lecture_add_new);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("강의정보를 올리고 있습니다.");
        myCache = new MyCache(this);

        lectureNameEditText = (EditText) findViewById(R.id.lecture_name_edit_text);
        lectureNameTextCount = (TextView) findViewById(R.id.lecture_name_text_count);
        new EditTextAndTextCount(lectureNameEditText, lectureNameTextCount, 100).setEditTextAndTextCount();

        professorNameEditText = (EditText) findViewById(R.id.lecture_professor_edit_text);
        professorNameTextCount = (TextView) findViewById(R.id.lecture_professor_text_count);
        new EditTextAndTextCount(professorNameEditText, professorNameTextCount, 50).setEditTextAndTextCount();

        majorEditText = (EditText) findViewById(R.id.lecture_major_edit_text);
        majorTextCount = (TextView) findViewById(R.id.lecture_major_text_count);
        new EditTextAndTextCount(majorEditText, majorTextCount, 30).setEditTextAndTextCount();

        addSubmitBtn = (Button) findViewById(R.id.lecture_add_submit);
        addSubmitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        addSubmitBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        addSubmitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        addSubmitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        submitLecture();
                        break;
                }
                return false;
            }
        });
    }

    public void submitLecture() {

        if (lectureNameEditText.getText().toString().trim().equalsIgnoreCase("") || professorNameEditText.getText().toString().trim().equalsIgnoreCase("")) {

            new AlertDialog.Builder(this)
                    .setMessage("강의명과 교수님 성함은 필수 입력사항입니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            return;
        }

        String lectureName = "", professorName = "", major = "";

        try {
            lectureName = new UTFConvert().convert(lectureNameEditText.getText().toString());
            professorName = new UTFConvert().convert(professorNameEditText.getText().toString());
            major = new UTFConvert().convert(majorEditText.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        progressDialog.show();
        new LectureAddAsyncTask(this).execute(new Security().WEB_ADDRESS + "add_lecture.php?user_id=" + myCache.getMyId() + "&device_id=" + myCache.getDeviceId() + "&school_id=" + myCache.getMySchoolId()
                + "&lecture_name=" + lectureName + "&professor_name=" + professorName + "&major=" + major
        );

    }

    public void overlapMessage() {
        new AlertDialog.Builder(this)
                .setMessage("중복되는 강의가 있습니다.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }

    public void afterAddLecture(int lectureId) {
        progressDialog.dismiss();
        Intent intent = new Intent().putExtra("itemId", String.valueOf(lectureId));
        setResult(1, intent);
        finish();
    }

}


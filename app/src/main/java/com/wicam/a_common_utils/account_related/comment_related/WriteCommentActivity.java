package com.wicam.a_common_utils.account_related.comment_related;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WebViewPage;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.ImageUploadAndDelete.ImageUploadingActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.io.UnsupportedEncodingException;


/**
 * Created by Hyeonmin on 2015-07-22.
 */
public class WriteCommentActivity extends ImageUploadingActivity {

    private CheckBox isReportCheckBox;
    private EditText commentEditText, urlEditText;
    private TextView commentTextCount, urlTextCount;
    private Button urlTest;
    private Button submit;
    private WriteCommentActivity writeCommentActivity = this;
    public String comment_id;
    private boolean wrote = false; // 두번 누르기 방지
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_common_write_comment);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("댓글을 올리고 있습니다.");

        isReportCheckBox = (CheckBox)findViewById(R.id.check_is_report);

        commentEditText = (EditText)findViewById(R.id.comment_edit_text);
        commentTextCount = (TextView)findViewById(R.id.comment_text_count);
        new EditTextAndTextCount(commentEditText, commentTextCount, 300).setEditTextAndTextCount();

        urlEditText = (EditText)findViewById(R.id.url_edit_text);
        urlTextCount = (TextView)findViewById(R.id.url_text_count);
        new EditTextAndTextCount(urlEditText, urlTextCount, 1000).setEditTextAndTextCount();

        urlTest = (Button)findViewById(R.id.url_test);
        urlTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlEditText.getText().toString().trim().equalsIgnoreCase(""))
                    return;
                startActivity(new Intent(WriteCommentActivity.this, WebViewPage.class).putExtra("url_link", getUrl(urlEditText.getText().toString())));
            }
        });

        imageToUpload = (ImageView)findViewById(R.id.image_to_be_uploaded);
        imageLoad = (Button)findViewById(R.id.image_load_button);
        imageLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] loadImageChoice = {"카메라로 찍기", "갤러리에서 선택하기"};
                new AlertDialog.Builder(writeCommentActivity).setTitle("올릴 이미지")
                        .setItems(loadImageChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(loadAndUploadImage.getTempImageFile()));
                                    intent.putExtra("return-data", true);
                                    startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
                                } else {
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(intent, REQ_CODE_PICK_GALLERY);
                                }
                            }
                        }).show();
            }
        });

        submit = (Button)findViewById(R.id.write_comment_summit);
        submit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        submit.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        submit.setTextColor(new WicamColors().TEXT_NORMAL);
                        submitContent();
                        break;
                }
                return false;
            }
        });
    }

    public void submitContent() {

        if (commentEditText.getText().toString().trim().equalsIgnoreCase("") && urlEditText.getText().toString().trim().equalsIgnoreCase("") && bitmapToUpload == null) {
            new AlertDialog.Builder(this)
                    .setMessage("내용, URL 링크, 이미지 중 하나는 포함되어야 합니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            return;
        }

        String nickname, comment, url;
        int report = isReportCheckBox.isChecked() ? 1 : 0;
        url = getUrl(urlEditText.getText().toString().trim());
        try {
            nickname = new UTFConvert().convert(new MyCache(this).getMyNickname());
            comment = new UTFConvert().convert(commentEditText.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

        if (wrote)
            return;
        wrote = true;
        progressDialog.show();
        new WriteConmmentAsyncTask(this).execute(new Security().WEB_ADDRESS + "write_comment.php?user_id=" + new MyCache(this).getMyId() + "&user_nickname=" + nickname
                + "&device_id=" + new MyCache(this).getDeviceId()
                + "&item_id=" + Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemId()
                + "&default_code=" + new MyCache(this).getDefaultCode() + "&content_id=" + new MyCache(this).getContentId()
                + "&content_type=" + new MyCache(this).getContentType()
                + "&report=" + String.valueOf(report) + "&comment=" + comment + "&url_link=" + url);
    }

    public void afterSubmittingContent() {
        progressDialog.dismiss();
        if (bitmapToUpload == null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(urlEditText.getWindowToken(), 0);
            finish();
        }
        else
            new Thread(new Runnable() {
                public void run() {
                    loadAndUploadImage.doPhotoUpload(comment_id + ".jpg");
                }
            }).start();
    }

    @Override
    protected void imageUploadDone() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(urlEditText.getWindowToken(), 0);
        finish();
    }

    public String getUrl(String url) {
        return url.equalsIgnoreCase("")? "" : url.contains("http://") ? url : "http://" + url;
    }
}
